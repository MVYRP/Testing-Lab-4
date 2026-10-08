package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для сжатия и восстановления строк с использованием алгоритма RLE.
 */
public class StringCompressor implements IStringCompressor {

    /**
     * Сжимает строку с использованием алгоритма RLE.
     *
     * @param text Входная строка для сжатия.
     * @return Строка в формате RLE.
     * @throws IllegalArgumentException если входная строка равна null, пуста или содержит цифры.
     */
    @Override
    public String InRLE(String text) throws IllegalArgumentException {
        validateInputText(text);

        StringBuilder compressedString = new StringBuilder();
        int count = 1;
        for (int i = 0; i < text.length(); i++) {
            if (i + 1 < text.length() && text.charAt(i) == text.charAt(i + 1)) {
                count++;
            } else {
                compressedString.append(count).append(text.charAt(i));
                count = 1;
            }
        }
        return compressedString.toString();
    }

    /**
     * Восстанавливает строку из формата RLE.
     *
     * @param rle Строка в формате RLE для восстановления.
     * @return Исходная строка.
     * @throws IllegalArgumentException если входная строка равна null или пуста.
     */
    @Override
    public String FromRLE(String rle) throws IllegalArgumentException {
        validateRLEString(rle);

        StringBuilder decompressedString = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\d+)(\\D)");
        Matcher matcher = pattern.matcher(rle);

        while (matcher.find()) {
            int count = Integer.parseInt(matcher.group(1));
            char character = matcher.group(2).charAt(0);
            for (int i = 0; i < count; i++) {
                decompressedString.append(character);
            }
        }
        return decompressedString.toString();
    }

    /**
     * Проверяет входную строку для сжатия.
     *
     * @param text Строка для проверки.
     * @throws IllegalArgumentException если строка невалидна.
     */
    private void validateInputText(String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Входная строка не может быть null.");
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой.");
        }
        if (text.matches(".*\\d+.*")) {
            throw new IllegalArgumentException("Входная строка не должна содержать цифр.");
        }
    }

    /**
     * Проверяет строку в формате RLE для восстановления.
     *
     * @param rle Строка RLE для проверки.
     * @throws IllegalArgumentException если строка невалидна.
     */
    private void validateRLEString(String rle) throws IllegalArgumentException {
        if (rle == null) {
            throw new IllegalArgumentException("Входная строка RLE не может быть null.");
        }
        if (rle.isEmpty()) {
            throw new IllegalArgumentException("Входная строка RLE не может быть пустой.");
        }
    }
}