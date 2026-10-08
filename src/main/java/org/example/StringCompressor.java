package org.example;

public class StringCompressor implements IStringCompressor {

    @Override
    public String InRLE(String text) throws IllegalArgumentException {
        validateInput(text, false); // false — числа не разрешены во входной строке

        if (text.isEmpty()) {
            return "";
        }

        StringBuilder compressed = new StringBuilder();
        char currentChar = text.charAt(0);
        int count = 1;

        for (int i = 1; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == currentChar && count < 9) {
                count++;
            } else {
                compressed.append(count).append(currentChar);
                currentChar = ch;
                count = 1;
            }
        }

        // Добавляем последний блок
        compressed.append(count).append(currentChar);

        return compressed.toString();
    }

    @Override
    public String FromRLE(String rle) throws IllegalArgumentException {
        validateInput(rle, true); // true — числа разрешены (и обязательны) в RLE-строке

        if (rle.isEmpty()) {
            return "";
        }

        StringBuilder decompressed = new StringBuilder();
        int i = 0;

        while (i < rle.length()) {
            // Извлекаем число (должна быть одна цифра от 1 до 9)
            if (i >= rle.length() || !Character.isDigit(rle.charAt(i))) {
                throw new IllegalArgumentException("Некорректный формат RLE: ожидается цифра на позиции " + i);
            }

            int count = rle.charAt(i) - '0';
            i++;

            if (count < 1 || count > 9) {
                throw new IllegalArgumentException("Количество повторений должно быть от 1 до 9, найдено: " + count);
            }

            // После цифры должен идти символ
            if (i >= rle.length()) {
                throw new IllegalArgumentException("Некорректный формат RLE: после цифры ожидается символ");
            }

            char ch = rle.charAt(i);
            if (Character.isDigit(ch)) {
                throw new IllegalArgumentException("Некорректный формат RLE: после цифры не может идти другая цифра");
            }

            decompressed.append(String.valueOf(ch).repeat(count));
            i++;
        }

        return decompressed.toString();
    }

    private void validateInput(String input, boolean allowDigits) {
        if (input == null) {
            throw new IllegalArgumentException("Входная строка не может быть null");
        }
        if (input.isEmpty()) {
            // Пустая строка разрешена — вернём пустую при сжатии/распаковке
            return;
        }

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                if (!allowDigits) {
                    throw new IllegalArgumentException(
                            "Входная строка не должна содержать цифры. Найдена цифра: '" + c + "'"
                    );
                }
            } else {
                if (!allowDigits && !Character.isLetter(c) && !isAllowedSymbol(c)) {
                    // Можно расширить список разрешённых символов
                    // По умолчанию допускаем буквы и некоторые знаки
                }
            }
        }
    }

    // Вспомогательный метод — какие символы (не цифры и не буквы) разрешены
    private boolean isAllowedSymbol(char c) {
        // Можно настроить под нужды: пробелы, знаки препинания и т.д.
        return " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".indexOf(c) != -1;
    }
}