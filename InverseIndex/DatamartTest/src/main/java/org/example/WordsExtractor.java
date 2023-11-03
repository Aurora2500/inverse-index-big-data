package org.example;

import dbManager.CreateTable;

public class WordsExtractor {
    public void filter(){
        new CreateTable().insertWordsTable("hola", "adios");
    }
}
