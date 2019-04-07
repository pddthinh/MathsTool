package com.pddthinh.mathsgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MathsGenerator {
    //region Definition
    private static final int MAX_RANDOM_DIGIT = 10;
    private static final int MAX_LINE_PER_PAGE = 18;
    private static final int MAX_COLUMN_PER_PAGE = 4;

    enum Operand {
        ADDITION("+"),
        SUBTRACTION("-"),
//        MULTIPLY("*"),
//        DIVISION("/"),

        ;

        private String mText;

        Operand(String text) {
            mText = text;
        }

        @Override
        public String toString() {
            return mText;
        }

    }

    class MathInfo {
        int value1;
        int value2;
        Operand operand;

        @Override
        public String toString() {
            return value1 + " " + operand + " " + value2;
        }
    }
    //endregion

    private List<MathInfo> mData = new ArrayList<>();

    //region Utility
    private MathInfo generateMath() {
        MathInfo data = new MathInfo();

        Random random = new Random(System.nanoTime());

        // generate operand
        data.operand = Operand.values()[random.nextInt(Operand.values().length)];

        // generate second op
        do {
            data.value1 = random.nextInt(MAX_RANDOM_DIGIT);
            data.value2 = random.nextInt(MAX_RANDOM_DIGIT);
        } while (data.value2 > data.value1 && data.operand == Operand.SUBTRACTION);

        return data;
    }

    private void write2File(List<MathInfo> data, int fileIdx) {
        File outFile = new File("/tmp", String.format(Locale.US, "%03d.csv", fileIdx));

        int column = 0;
        int counter = 1;
        int itemPerPage = MAX_COLUMN_PER_PAGE * MAX_LINE_PER_PAGE;
        StringBuilder buffer = new StringBuilder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            for (MathInfo m : data) {
                if (buffer.length() == 0)
                    column = 0;

                buffer.append(counter + MAX_LINE_PER_PAGE * column).append("); ")
                        .append(m.toString()).append(" =;")
                ;

                column++;

                if (column >= MAX_COLUMN_PER_PAGE) {
                    writer.write(buffer.toString());
                    writer.newLine();
                    writer.write(";\n;\n");

                    buffer.setLength(0);
                    counter ++;

                    continue;
                }

                if (counter + MAX_LINE_PER_PAGE * column > itemPerPage) {
                	buffer.setLength(0);
                	column = 0;
                	counter = 1;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //endregion

    private void execute(int numFile) {
        for (int j = 0; j < numFile; j++) {
            for (int i = 0; i < 2 * ((MAX_LINE_PER_PAGE + 1) * MAX_COLUMN_PER_PAGE); i++)
                mData.add(generateMath());

            write2File(mData, j);
        }
    }

    //region Main function
    public static void main(String[] arguments) {
        MathsGenerator generator = new MathsGenerator();

        int numFile;
        try {
            numFile = Integer.parseInt(arguments[0]);
        } catch (Exception ex) {
            // no-op
            numFile = 1;
        }

        generator.execute(numFile);
    }
    //endregion
}
