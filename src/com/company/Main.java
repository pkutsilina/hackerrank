package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Main {

    public static final int MAX_GSM_7 = 160;
    public static final Double COST_GSM_7 = 0.01;
    public static final int MAX_UCS_2 = 70;
    public static final Double COST_UCS_2 = 0.015;
    public static final Pattern P = Pattern.compile("^[a-zA-Z0-9\\s\\.,]*$");

    private static Integer totalLength;
    private static Double totalCost = 0.0;

    public static void main(String[] args) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get("/Users/palina_kutsilina/IdeaProjects/test/temp.txt")));
        String[] dataArray = data.split("\\s");
        int[] ints = new int[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            ints[i] = Integer.parseInt(dataArray[i]);
        }
        System.out.println("length: " + dataArray.length);
//        System.out.println(activityNotifications(new int[] {2, 3, 4, 2, 3, 6, 8, 4, 5}, 5));
        System.out.println(activityNotifications(ints, 10000));
    }


    public static double calculateTotalMessageCost(List<String> messages) {
        double result = 0;
        for (int i = 0; i < messages.size(); i++) {
            result += calculateMessageCost(messages.get(i));
        }
        return result;
    }

    private static double calculateMessageCost(String message) {
        totalLength = message.length();
        Matcher m = P.matcher(message);
        if (m.matches()) {
            if (message.length() < MAX_GSM_7) {
                return COST_GSM_7;
            } else {
                findBestSubstring(message);
                return totalCost;
            }
        }
        return totalCost;
    }

    private static void findBestSubstring(String message) {
        if (message == null || message.length() == 0) {
            return;
        }
        List<Integer> indexesOfIrregular = new ArrayList<Integer>();
        for (int i = 0; i < message.length(); i++) {
            Matcher matcher = P.matcher(String.valueOf(message.charAt(i)));
            if (!matcher.matches()){
                indexesOfIrregular.add(i);
            }
        }
        if (indexesOfIrregular.size() == 0) {
            if (message.length() % MAX_GSM_7 == 0) {
                totalCost += (message.length() / MAX_GSM_7) * COST_GSM_7;
                return;
            } else {
                totalCost += (message.length() / MAX_GSM_7 + 1) * COST_GSM_7;
                return;
            }
        }
        Integer maximumIndex = -1;
        Integer itsMaximumBorder = -1;
        Integer maximumNumberOfIrregulars = -1;
        List<Integer> indexesOfMax = null;

        for (int i = 0; i < indexesOfIrregular.size(); i++) {
            ArrayList<Integer> localMaxIndexes;
            Integer currentIndex = indexesOfIrregular.get(i);
            int leftBoundery = Math.max(currentIndex - MAX_UCS_2 - 1, 0);
            int leftNumberOfAdditionalIrregular = 0;
            ArrayList<Integer> localLeftIndexes = new ArrayList<>();
            int rightBoundery = currentIndex + MAX_UCS_2 - 1;
            int rightNumberOfAdditionalIrregular = 0;
            ArrayList<Integer> localRightIndexes = new ArrayList<>();

            for (int j = i; j < indexesOfIrregular.size(); j++) {
                if (indexesOfIrregular.get(j) <= rightBoundery) {
                    rightNumberOfAdditionalIrregular++;
                    localLeftIndexes.add(j);
                } else {
                    break;
                }
            }
            for (int j = i; j >= 0; j--) {
                if (indexesOfIrregular.get(j) >= leftBoundery) {
                    leftNumberOfAdditionalIrregular++;
                    localRightIndexes.add(j);
                }
                else {
                    break;
                }
            }
            int localMaximum = -1;
            int localBorder = -1;
            if (rightNumberOfAdditionalIrregular > leftNumberOfAdditionalIrregular) {
                localMaximum = rightNumberOfAdditionalIrregular;
                localBorder = rightBoundery;
                localMaxIndexes = localRightIndexes;
            } else {
                localMaximum = leftNumberOfAdditionalIrregular;
                localBorder = leftBoundery;
                localMaxIndexes = localLeftIndexes;
            }
            if (localMaximum > maximumIndex) {
                maximumNumberOfIrregulars = localMaximum;
                maximumIndex = i;
                itsMaximumBorder = localBorder;
                indexesOfMax = localMaxIndexes;
            }
        }
        indexesOfIrregular.removeAll(indexesOfMax);
        totalLength -= MAX_UCS_2;
        if (totalLength < 0) {
            totalLength = 0;
        }
        totalCost += COST_UCS_2;
        int upper = Math.max(maximumIndex, itsMaximumBorder);
        int lower = Math.min(maximumIndex, itsMaximumBorder);
        findBestSubstring(message.substring(0, lower));
        findBestSubstring(message.substring(lower, upper));
        return;
    }

    static int activityNotificationsIn(int[] expenditure, int d) {
        int[] initialArray = expenditure.clone();
        int[] lastDDays = new int[d];
        int result = 0;
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                if (expenditure[j] > expenditure[j+1]) {
                    int temp = expenditure[j];
                    expenditure[j] = expenditure[j+1];
                    expenditure[j+1] = temp;
                }
            }
        }
        for (int i = 0; i < d; i++) {
            lastDDays[i] = expenditure[i];
        }

        result += ifOver(lastDDays, d, expenditure[d]);

        for (int i = d + 1; i < expenditure.length; i++) {
            int currentElement = expenditure[i];
            int previousElement = expenditure[i - 1];
            int previousElementShouldBeIndex = -1;
            int toRemove = initialArray[i - d - 1];
            int idxToRemove = -1;

            //finding position toRemove and position to add new one
            for (int j = 0; j < d; j++) {
                if (lastDDays[j] == toRemove) {
                    idxToRemove = j;
                }
                if (previousElement  <= lastDDays[j]) {
                    previousElementShouldBeIndex = j;
                }
            }


            //moving elements deleting toRemove and adding new one
            int idxToInsert = previousElementShouldBeIndex == -1 ? d - 1 : previousElementShouldBeIndex;

            if (idxToRemove < idxToInsert) {
                for (int j = idxToRemove; j < idxToInsert; j++) {
                    lastDDays[j] = lastDDays[j + 1];
                }
                lastDDays[idxToInsert] = previousElement;
            } else if (idxToRemove > idxToInsert) {
                for (int j = toRemove - 1; j >= idxToInsert; j--) {
                    lastDDays[j + 1] = lastDDays[j];
                }
                lastDDays[idxToInsert] = previousElement;
            } else if (idxToInsert == idxToRemove) {
                lastDDays[idxToRemove] = previousElement;
            }

            result += ifOver(lastDDays, d, currentElement);

        }
        return result;
    }

    static int activityNotifications(int[] expenditure, int d) {
        int result = 0;
        int prevLast = -1;
        int prevMedianCaseOne = -1;
        int prevMedianCaseTwoL = -1;
        int prevMedianCaseTwoR = -1;
        for (int i = d; i < expenditure.length; i++) {
            int today = expenditure[i];
            boolean canUsePrevious = false;
            if (i != d) {
                int newOne = expenditure[i - 1];
                int toRemoveOne = expenditure[i - d - 1];
                if (d % 2 == 1) {
                    if ((prevMedianCaseOne - toRemoveOne) * (prevMedianCaseOne - newOne) >= 0) {
                        canUsePrevious = true;
//                        System.out.println("use");
                        if (today >= prevMedianCaseOne * 2) {
                            result++;
                        }
                    }
                } else {
                    if ((toRemoveOne < prevMedianCaseTwoL && toRemoveOne < prevMedianCaseTwoR
                            && newOne < prevMedianCaseTwoL && newOne < prevMedianCaseTwoR) ||
                            (toRemoveOne > prevMedianCaseTwoL && toRemoveOne > prevMedianCaseTwoR
                                    && newOne > prevMedianCaseTwoL && newOne > prevMedianCaseTwoR)) {
                        canUsePrevious = true;
//                        System.out.println("use");
                        if (today >= prevMedianCaseTwoL + prevMedianCaseTwoR) {
                            result++;
                        }
                    }
                }
            }
            if (!canUsePrevious) {
                int[] lastDays = new int[d];
                //fill lastDays
                for (int j = i - d, k = 0; j < i; j++, k++) {
                    lastDays[k] = expenditure[j];
                }
                //do actual counting
                if (d % 2 == 1) {
                    int position = d / 2;
                    int median = findAtPosition(position, d, lastDays);
                    prevMedianCaseOne = median;
                    if (today >= median * 2) {
                        result++;
                    }
                } else {
                    int rightPosition = d / 2;
                    int leftPosition = d / 2 - 1;
                    int median1 = findAtPosition(leftPosition, d, lastDays);
                    int median2 = findAtPosition(rightPosition, d, lastDays);
                    prevMedianCaseTwoL = median1;
                    prevMedianCaseTwoR = median2;
                    if (today >= median1 + median2) {
                        System.out.println(median1 + median2);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    static int findAtPosition(int position, int d, int[] arr) {
        for (int k = arr.length - 1; k >= 0; k--) {
            int pivot = arr[k];
            int j = 0;
            int i = -1;
            while (j < k) {
                    if (arr[j] <= pivot) {
                        i++;
                        int temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                    }
                    j++;
            }
            //put on right place pivot
            for (int l = k - 1; l > i; l--) {
                arr[l+1] = arr[l];
            }
            arr[i+1] = pivot;
            if (i+1 == position) {
                return pivot;
            } else {
                if (i+1 > position) {
                    return findAtPosition(position, d, Arrays.copyOfRange(arr, 0, i+1));
                } else {
                    int shiftedPosition = position - (i + 1 + 1);
                    return findAtPosition(shiftedPosition, d, Arrays.copyOfRange(arr, i + 1 + 1, arr.length));
                }
            }
        }
        return Integer.MAX_VALUE;
    }

        private static int ifOver(int[] lastDDays, int d, int currentDay) {
        int maxPossibleNotifs = 0;
        if (d % 2 == 0) {
            maxPossibleNotifs = lastDDays[d / 2 - 1] + lastDDays[d / 2];
        } else {
            maxPossibleNotifs = lastDDays[d / 2] * 2;
        }

        if (currentDay >= maxPossibleNotifs) {
           return 1;
        }
        return 0;
    }
}
