package util.strategy;

import util.LexMapping;

import java.util.Map;

abstract class AStrategy {

    @Deprecated
    void outputData (String query, LexMapping lexMapping, Map<Integer, String> mappingData, int[] intStore) {

        int prev = 0;

        System.out.println(query);
        System.out.println(lexMapping.getNoDocuments());
        for (int i = 0; i < intStore.length; i++) {
            if (i % 2 == 0) {
                String rawDocName = mappingData.get(intStore[i] + prev);
                prev = intStore[i] + prev;
                System.out.print(rawDocName + " ");
            }
            else {
                System.out.println(intStore[i]);
            }
        }
    }

}
