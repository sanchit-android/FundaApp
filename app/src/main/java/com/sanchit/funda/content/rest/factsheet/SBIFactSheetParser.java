package com.sanchit.funda.content.rest.factsheet;

import android.graphics.RectF;

import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.content.rest.AbstractRestEnricher;
import com.sanchit.funda.model.MutualFund;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripperByArea;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class SBIFactSheetParser extends AbstractFactSheetParser {

    private static String sectionStart = "•";
    private static String dropPart1 = "`";

    private final CacheManager.Cache<String, MutualFund> funds;

    public SBIFactSheetParser() {
        funds = CacheManager.get(Caches.FUNDS_BY_NAME, MutualFund.class);
    }

    @Override
    public Void enrich(String input) {
        String path = "https://www.sbimf.com/en-us/Lists/SchemeFactsheets/August%202020.pdf";
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            PDDocument document = PDDocument.load(is);

            parseDocument(document);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String extractFundName(String text) {
        String[] lines = text.split("\n");

        String value = lines[0];

        String previous = lines[0];

        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];
            if (l.startsWith("This product is suitable for investors")) {
                value = previous;
                break;
            } else {
                previous = l;
            }
        }

        return value;
    }

    private String extractInfoPart1(String text, String key, boolean sameLineValue) {
        String[] lines = text.split("\n");
        boolean begin = false;
        StringBuffer currentText = new StringBuffer("");
        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];
            if (l.contains(key)) {
                begin = true;
                if (sameLineValue) {
                    String[] splits = l.split(": ");
                    if (splits.length > 1) {
                        currentText.append(splits[1]).append(" ");
                    }
                }
                continue;
            }
            if (begin) {
                if (l.startsWith(sectionStart)) {
                    break;
                }
                currentText.append(l).append(" ");
            }
        }
        String value = cleanup(currentText);
        System.out.println(key + "-" + value);
        return value;
    }

    private String cleanup(StringBuffer currentText) {
        String value = currentText.toString();
        value = value.replace("\n", "").replace("\r", "");
        value = value.replace(dropPart1, "");
        value = value.trim();
        return value;
    }


    private void parseDocument(PDDocument document) throws IOException {
        for (int page = 1; page <= document.getNumberOfPages(); ++page) {
            System.out.println("===============" + page + "======================");
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            stripper.addRegion("class1", new RectF(0, 210, 151, 560));
            stripper.addRegion("fundName", new RectF(254, 516, 554, 781));
            stripper.extractRegions(document.getPage(page));
            String pageText = stripper.getTextForRegion("class1");
            String fundNameRegionData = stripper.getTextForRegion("fundName");

            if (!pageText.contains("Benchmark")) {
                continue;
            }

            String fundName = extractFundName(fundNameRegionData);
            if (!fundName.contains("SBI")) {
                continue;
            }

            System.out.println("Fund Name" + "-" + fundName);
            MutualFund fund = funds.get(fundName);

            if (fund == null) {
                continue;
            }

            extractInfoPart1(pageText, "Benchmark", true);
            extractInfoPart1(pageText, "AAUM for the Month of July 2020", false);
            extractInfoPart1(pageText, "Fund Manager", true);
            extractInfoPart1(pageText, "Exit Load", true);
            extractInfoPart1(pageText, "Type of Scheme", true);
            //System.out.println(pageText);
        }
    }
}
