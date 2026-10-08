package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModuleTest {

    private IStringCompressor compressor;

    @BeforeEach
    void setUp() {
        compressor = new StringCompressor();
    }

    @Test
    void testInRLE_BasicCompression() {
        // Отправляется: строка "aaaaabbbcccccccoooooooo"
        String input = "aaaaabbbcccccccoooooooo";
        String result = compressor.InRLE(input);

        boolean isValidFormat = result.equals("a5b3c7o8") || result.equals("5a3b7c8o");
        Assertions.assertTrue(isValidFormat, "Кодирование должно вернуть a5b3c7o8 или 5a3b7c8o. Фактически: " + result);
    }

    @Test
    void testInRLE_NullString() {
        // Отправляется: null
        // Ожидается: возврат ошибки (IllegalArgumentException)
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.InRLE(null);
        }, "При передаче null должна выбрасываться IllegalArgumentException");
    }

    @Test
    void testInRLE_EmptyString() {
        // Отправляется: пустая строка ""
        // идеальное поведение — выброс ошибки.
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.InRLE("");
        }, "При передаче пустой строки должна выбрасываться IllegalArgumentException");
    }

    @Test
    void testInRLE_MixedCase() {
        // Отправляется: "аааААА"
        String input = "аааААА";
        String result = compressor.InRLE(input);

        // Регистр имеет значение, символы не должны смешиваться
        boolean isValidFormat = result.equals("а3А3") || result.equals("3а3А");
        Assertions.assertTrue(isValidFormat, "Кодирование смешанного регистра должно быть корректным. Фактически: " + result);
    }

    @Test
    void testInRLE_SingleCharacter() {
        // Отправляется: "A"
        String input = "A";
        String result = compressor.InRLE(input);

        boolean isValidFormat = result.equals("A1") || result.equals("1A") || result.equals("A");
        Assertions.assertTrue(isValidFormat, "Символ без повторений должен кодироваться корректно. Фактически: " + result);
    }

    @Test
    void testInRLE_ContainsNumbers() {
        // "входные данные не должны содержать чисел"
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.InRLE("abc123def");
        }, "Строка с числами должна вызывать IllegalArgumentException");
    }

    @Test
    void testInRLE_ManyIdenticalChars() {
        // Отправляется: 640 символов 'A'
        String input = "A".repeat(640);
        String result = compressor.InRLE(input);

        boolean isValidFormat = result.equals("A640") || result.equals("640A");
        // Grok тут упадет (он выдаст кучу девяток), что отлично отобразится в GitHub Actions!
        Assertions.assertTrue(isValidFormat, "Большое количество символов должно сжиматься в одно число.");
    }

    //тесты распаковки

    @Test
    void testFromRLE_NullString() {
        // Отправляется: null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.FromRLE(null);
        }, "При декодировании null должна выбрасываться IllegalArgumentException");
    }

    @Test
    void testFromRLE_EmptyString() {
        // Отправляется: пустая строка ""
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.FromRLE("");
        }, "При декодировании пустой строки должна выбрасываться IllegalArgumentException");
    }

    @Test
    void testFromRLE_InvalidFormat_SingleChar() {
        // Отправляется: "A" (без цифры)
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            compressor.FromRLE("A");
        }, "Передача одиночного символа без числа должна вызывать ошибку формата");
    }

    @Test
    void testFromRLE_LargeNumber() {
        // Отправляется: "A640"
        try {
            String result = compressor.FromRLE("A640");
            Assertions.assertEquals("A".repeat(640), result, "Должно восстановиться 640 символов 'A'");
        } catch (IllegalArgumentException e) {
            // Если нейросеть ждет формат "640A"
            String alternateResult = compressor.FromRLE("640A");
            Assertions.assertEquals("A".repeat(640), alternateResult, "Должно восстановиться 640 символов 'A'");
        }
    }
}