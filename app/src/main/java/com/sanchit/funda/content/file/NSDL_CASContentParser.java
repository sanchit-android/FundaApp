package com.sanchit.funda.content.file;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.NumberUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NSDL_CASContentParser extends AbstractFileParser<MFPosition> {

    private static final String NA = "NOT AVAILABLE";

    private List<MFPosition> cleanupPositions(List<MFPosition> positions) {
        for (MFPosition position : positions) {

        }
        return positions;
    }

    private List<MFPosition> extractPositions(List<List<String>> parseableLines) {
        List<MFPosition> positions = new ArrayList<>();
        for (List<String> section : parseableLines) {
            StringBuffer fundName = new StringBuffer();
            MFPosition position = new MFPosition();
            MutualFund fund = new MutualFund();
            position.setFund(fund);

            int folioIndex = 0;

            for (int i = 0; i < section.size(); ++i) {
                String line = section.get(i);
                line = line.trim();

                if (i == 0) {
                    String[] words = line.split(" ");
                    for (int j = 1; j < words.length; j++) {
                        if (!isNumeric(words[j]) || words[j].length() <= 3) {
                            fundName.append(words[j]).append(" ");
                        } else {
                            folioIndex = j;
                            break;
                        }
                    }
                    position.getFund().setIsin(words[0]);
                    position.setFolioNo(words[folioIndex]);
                    position.setQuantity(NumberUtils.parseNumber(words[folioIndex + 1]));
                    position.setCostPrice(NumberUtils.parseNumber(words[folioIndex + 2]));
                    position.setCost(NumberUtils.parseNumber(words[folioIndex + 3]));
                    position.setCurrentNAV(NumberUtils.parseNumber(words[folioIndex + 4]));
                    position.setCurrentValue(NumberUtils.parseNumber(words[folioIndex + 5]));
                    position.setUnrealizedProfit(NumberUtils.parseNumber(words[folioIndex + 6]));
                    if (words.length > folioIndex + 7) {
                        position.setAnnualizedReturn(NumberUtils.parseNumber(words[folioIndex + 7]).divide(new BigDecimal(100)));
                    }
                } else {
                    fundName.append(line);
                }
            }

            fund.setFundName(fundName.toString());
            positions.add(position);
        }
        return positions;
    }

    private List<List<String>> cleanupParseableData(List<List<String>> parseableLines) {
        List<List<String>> cleanedUpLines = new ArrayList<List<String>>();
        for (List<String> section : parseableLines) {
            List<String> cleanedUpSection = new ArrayList<String>();
            for (int i = 0; i < section.size(); ++i) {
                String line = section.get(i);
                line = line.trim();
                if (line.startsWith("Total")) {
                    break;
                }

                if (i == 1) {
                    if (line.startsWith(NA)) {
                        line = line.substring(NA.length());
                    } else {
                        line = line.substring(line.indexOf(" "));
                    }
                }

                cleanedUpSection.add(line.trim());
            }
            cleanedUpLines.add(cleanedUpSection);
        }
        return cleanedUpLines;
    }

    private void extractDatasetToParse(String pageText, List<List<String>> parseableLines) {
        String[] lines = pageText.split("\n");
        boolean fundParsingStart = false;
        List<String> runningParseableLinesSet = new ArrayList<String>();
        for (String line : lines) {
            // System.out.println(line);
            if (line.startsWith("Total")) {
                // break;
            }

            if (line.startsWith("INF")) {
                fundParsingStart = true;

                if (runningParseableLinesSet.size() > 0) {
                    parseableLines.add(runningParseableLinesSet);
                    runningParseableLinesSet = new ArrayList<>();
                }
            }

            if (fundParsingStart) {
                runningParseableLinesSet.add(line);
            }
        }

        if (runningParseableLinesSet.size() > 0) {
            parseableLines.add(runningParseableLinesSet);
        }
    }

    @Override
    public List<MFPosition> parse(Activity activity, Uri uri) throws IOException {
        if (!checkPermission(activity)) {
            requestPermission(activity);
        }

        if (!checkPermission(activity)) {
            Toast.makeText(activity, "Permissions Missing", Toast.LENGTH_LONG).show();
            return null;
        }

        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri)) {
            PDDocument document = PDDocument.load(inputStream, "BPLPS4073L");
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            boolean beginParsing = false;
            int occuranceOfStartText = 0;
            List<List<String>> parseableLines = new ArrayList<>();

            for (int page = 1; page <= document.getNumberOfPages(); ++page) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String pageText = stripper.getText(document);

                if (!beginParsing) {
                    if (pageText.contains("Mutual Fund Folios (F)")) {
                        occuranceOfStartText++;
                    }

                    if (occuranceOfStartText == 2) {
                        beginParsing = true;
                    }

                    if (!beginParsing) {
                        continue;
                    }
                }

                extractDatasetToParse(pageText, parseableLines);
            }

            parseableLines = cleanupParseableData(parseableLines);
            List<MFPosition> positions = extractPositions(parseableLines);
            positions = cleanupPositions(positions);
            return positions;
        }
    }


}
