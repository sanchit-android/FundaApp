package com.sanchit.funda.content.file;

import android.app.Activity;
import android.net.Uri;

import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.log.LogManager;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DateUtils;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.SecurityUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

public class GrowwStatementParser extends AbstractFileParser<MFTrade> {

    private static final String NA = "NOT AVAILABLE";
    private final String PAN;

    private final CacheManager.Cache<String, MutualFund> fundsByName;
    private final CacheManager.Cache<String, MutualFund> fundsByAmfiId;

    private Map<String, String[]> mergedFunds = new HashMap<>();

    public GrowwStatementParser(String PAN) {
        this.PAN = PAN;
        fundsByName = CacheManager.get(Caches.FUNDS_BY_NAME, MutualFund.class);
        fundsByAmfiId = CacheManager.get(Caches.FUNDS_BY_AMFI_ID, MutualFund.class);
        mergedFunds.put("133816", new String[]{"119779", "27/1/2020", "25/2/2020", "0.659877", "17.82"});
    }

    @Override
    public List<MFTrade> parse(Activity activity, Uri uri) throws Exception {
        SecurityUtils.setupPermissions(activity);
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        PDDocument document = PDDocument.load(inputStream, PAN);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);

        List<String> parseableLines = new ArrayList<>();
        boolean start = false;

        for (int page = 1; page <= document.getNumberOfPages(); ++page) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            String pageText = stripper.getText(document);

            String[] lines = pageText.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("Transactions from") && i < lines.length
                        && lines[i + 1].contains("Scheme Name")) {
                    start = true;
                    i++;
                } else if (lines[i].contains("*XIRR")) {
                    start = false;
                } else if (start) {
                    parseableLines.add(lines[i]);
                }
            }

            if (!start) {
                continue;
            }
        }

        List<List<String>> parseableGroups = new ArrayList<>();
        List<String> group = new ArrayList<String>();
        for (int i = 0; i < parseableLines.size(); i++) {
            if (group.isEmpty()) {
                group.add(parseableLines.get(i));
                continue;
            } else if (parseableLines.get(i).contains("PURCHASE") || parseableLines.get(i).contains("REDEEM")) {
                parseableGroups.add(group);
                group = new ArrayList<String>();
                group.add(parseableLines.get(i));
            } else {
                group.add(parseableLines.get(i));
            }
        }

        if (!group.isEmpty()) {
            parseableGroups.add(group);
        }

        List<MFTrade> trades = new ArrayList<>();
        for (int i = 0; i < parseableGroups.size(); i++) {
            MFTrade trade = parse(parseableGroups.get(i));
            if (trade != null) {
                trades.add(trade);
            }
        }

        // drop the redeem/sell/close transactions from end
        ListIterator<MFTrade> itr = trades.listIterator(trades.size());
        while (itr.hasPrevious()) {
            MFTrade t = itr.previous();
            if (Constants.Side.SELL.equals(t.getSide())) {
                itr.remove();
            } else {
                break;
            }
        }

        document.close();
        inputStream.close();
        return trades;
    }

    private MFTrade parse(List<String> group) throws ParseException {
        StringBuffer namePart = new StringBuffer();
        String dataPart = "";
        String side = Constants.Side.BUY;
        for (int i = 0; i < group.size(); i++) {
            String text = group.get(i);

            if (text.contains("PURCHASE")) {
                String splits[] = text.split(" PURCHASE ");
                namePart.append(splits[0]);
                dataPart = splits[1];
            } else if (text.contains("REDEEM")) {
                String splits[] = text.split(" REDEEM ");
                namePart.append(splits[0]);
                dataPart = splits[1];
                side = Constants.Side.SELL;
            } else {
                text = text.split("\n")[0].split("\r")[0].trim();
                namePart.append(" ").append(text);
            }
        }

        String fundName = extractName(namePart);
        MutualFund fund = fundsByName.get(fundName.toLowerCase());

        if (fund == null) {
            //LogManager.log("Fund not found - FundName[" + fundName + "], namePart[" + namePart + "]");
            String origFundName = identifyCorrection(fundName);
            fund = fundsByName.get(origFundName.toLowerCase());
            if (fund == null) {
                LogManager.log("Fund not found - FundName[" + fundName + "], origFundName[" + origFundName + "], namePart[" + namePart + "]");
                return null;
            }
        }

        MFTrade trade = new MFTrade();
        trade.setFund(fund);
        trade.setSide(side);
        extractDataIntoTrade(trade, dataPart);
        identifyAnyFundMerges(trade);
        trade.setUID(UUID.randomUUID().toString());
        return trade;
    }

    private void identifyAnyFundMerges(MFTrade trade) throws ParseException {
        if (mergedFunds.containsKey(trade.getFund().getAmfiID())) {
            String[] mergedFundData = mergedFunds.get(trade.getFund().getAmfiID());

            MutualFund newFund = fundsByAmfiId.get(mergedFundData[0]);
            trade.setFund(newFund);
            trade.setInvestmentDate(DateUtils.parseDate(mergedFundData[2]));
            trade.setQuantity(trade.getQuantity().multiply(new BigDecimal(mergedFundData[3]), new MathContext(4)));
            trade.setCostPrice(new BigDecimal(mergedFundData[4]));
            trade.setCost(trade.getCostPrice().multiply(trade.getQuantity()));
        }
    }

    private String identifyCorrection(String fundName) {
        if (fundName.startsWith("DHFL Pramerica")) {
            fundName = "PGIM India" + fundName.substring("DHFL Pramerica".length());
        }
        if (fundName.contains("Offshore")) {
            fundName = fundName.replace("Offshore", "Off-shore");
        }
        if (fundName.contains("Off shore")) {
            fundName = fundName.replace("Off shore", "Off-shore");
        }
        if (fundName.contains("U S")) {
            fundName = fundName.replace("U S", "U. S.");
        }
        if (fundName.contains("US")) {
            fundName = fundName.replace("US", "U. S.");
        }
        if (fundName.contains("Multi Cap")) {
            fundName = fundName.replace("Multi Cap", "Multicap");
        }
        if (fundName.contains("Feeder Franklin")) {
            fundName = fundName.replace("Feeder Franklin", "Feeder - Franklin");
        }
        if (fundName.contains("FOF")) {
            fundName = fundName.replace("FOF", "Fund of Fund");
        }
        if (fundName.contains("Government Securities Fund")) {
            fundName = fundName.replace("Government Securities Fund", "GSF");
        }
        if (fundName.contains("Franklin Infotech")) {
            fundName = fundName.replace("Franklin Infotech", "Franklin India Technology");
        }
        return fundName;
    }

    private String extractName(StringBuffer namePart) {
        String temp = namePart.toString();
        if (temp.contains("Direct")) {
            temp = temp.substring(0, temp.indexOf("Direct"));
        }
        if (temp.contains("DIRECT")) {
            temp = temp.substring(0, temp.indexOf("DIRECT"));
        }
        temp = temp.trim();
        if (temp.endsWith("-")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        temp = temp.trim();
        if (!temp.contains("Fund") && !temp.contains("FUND") && !temp.endsWith("FOF")) {
            temp = temp + " Fund";
        }
        return temp;
    }

    private void extractDataIntoTrade(MFTrade trade, String dataPart) throws ParseException {
        String[] parts = dataPart.split(" ");

        String unitsPart = parts[0];
        String navPart = parts[1];
        String amountPart = parts[2];
        String datePart = parts[3];
        String monthPart = parts[4];
        String yearPart = parts[5];

        trade.setQuantity(NumberUtils.parseNumber(unitsPart));
        trade.setCostPrice(NumberUtils.parseNumber(navPart));
        trade.setCost(NumberUtils.parseNumber(amountPart));
        trade.setInvestmentDate(DateUtils.parseDate(datePart + monthPart + yearPart, "ddMMMyyyy"));
    }
}