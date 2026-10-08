package org.example;

public class StringCompressor implements IStringCompressor {

    @Override
    public String InRLE(String text) {
        // Валидация входных данных
        validateInput(text);
        validateNoDigits(text);

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой");
        }

        StringBuilder result = new StringBuilder();
        int count = 1;
        char currentChar = text.charAt(0);

        for (int i = 1; i < text.length(); i++) {
            char nextChar = text.charAt(i);

            if (nextChar == currentChar) {
                count++;
            } else {
                // Добавляем текущий символ и его количество
                appendEncoded(result, currentChar, count);
                currentChar = nextChar;
                count = 1;
            }
        }

        // Добавляем последний символ
        appendEncoded(result, currentChar, count);

        return result.toString();
    }

    @Override
    public String FromRLE(String rle) {
        // Валидация входных данных
        validateInput(rle);

        if (rle.isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой");
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < rle.length()) {
            char currentChar = rle.charAt(i);

            // Проверяем, что текущий символ - буква (не цифра)
            if (Character.isDigit(currentChar)) {
                throw new IllegalArgumentException("Некорректный формат RLE: ожидалась буква, найдена цифра '" + currentChar + "'");
            }

            i++; // Переходим к следующему символу (должен быть цифрой)

            // Проверяем, не достигли ли конца строки
            if (i >= rle.length()) {
                throw new IllegalArgumentException("Некорректный формат RLE: отсутствует число после символа '" + currentChar + "'");
            }

            // Собираем число (может быть многозначным)
            StringBuilder countStr = new StringBuilder();
            while (i < rle.length() && Character.isDigit(rle.charAt(i))) {
                countStr.append(rle.charAt(i));
                i++;
            }

            if (countStr.length() == 0) {
                throw new IllegalArgumentException("Некорректный формат RLE: отсутствует число после символа '" + currentChar + "'");
            }

            // Парсим количество повторений
            int count;
            try {
                count = Integer.parseInt(countStr.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректный формат RLE: число '" + countStr + "' слишком большое");
            }

            if (count <= 0) {
                throw new IllegalArgumentException("Некорректный формат RLE: количество повторений должно быть положительным, найдено: " + count);
            }

            // Добавляем символ count раз
            for (int j = 0; j < count; j++) {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    /**
     * Вспомогательный метод для добавления закодированной последовательности
     */
    private void appendEncoded(StringBuilder result, char character, int count) {
        result.append(character);
        if (count > 1) {
            result.append(count);
        }
    }

    /**
     * Проверка входных данных на null
     */
    private void validateInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Входная строка не может быть null");
        }
    }

    /**
     * Проверка, что строка не содержит цифр
     */
    private void validateNoDigits(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                throw new IllegalArgumentException("Входная строка не должна содержать цифры");
            }
        }
    }
}