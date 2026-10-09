#!/usr/bin/env python3
import yaml
import argparse
from pathlib import Path
from typing import Dict, Any

# Шаблоны для Java кода
TEST_FILE_TEMPLATE = """// =============================================================
// AUTO-GENERATED TESTS. DO NOT EDIT MANUALLY.
// Source: {spec_source}
// Generator: gen_tests.py v1.0
// =============================================================
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class {module_name}GeneratedTest {{
    private I{module_name} _sut;

    @BeforeEach
    public void setUp() {{
        _sut = new {module_name}();
    }}

{test_methods}
}}
"""

TEST_METHOD_TEMPLATE = """
    @Test
    @DisplayName("Класс эквивалентности: {case_desc}")
    public void test_{method_name}_{case_name}() {{
        // === Arrange ===
        // Предусловие: {pre}
        // Ожидается: {expected}

        // === Act & Assert ===
        // Постусловие: {post}
        {act_assert_code}
    }}
"""

def format_java_input(value: Any) -> str:
    if value is None: return "null"
    if isinstance(value, str):
        if value.startswith('"') and value.endswith('"'):
            return value
        return f'"{value}"'
    if isinstance(value, bool): return "true" if value else "false"
    return str(value)

def generate_method_tests(method_data: Dict[str, Any]) -> list:
    case_blocks = []
    for eq_class in method_data.get("equivalence_classes", []):
        inputs_str = ", ".join(format_java_input(inp) for inp in eq_class["inputs"])
        method_name = method_data["name"]

        # Если ожидается исключение
        if "Exception" in str(eq_class["expected"]):
            act_assert = f"""assertThrows(IllegalArgumentException.class, () -> {{
            _sut.{method_name}({inputs_str});
        }});\n"""
        else:
            # Обычный вызов
            act_assert = f"""String result = _sut.{method_name}({inputs_str});
        // TODO: Замените на реальную проверку. Сгенерированная заглушка:
        assertTrue(true, "Сгенерированная проверка");"""

        case_blocks.append(
            TEST_METHOD_TEMPLATE.format(
                case_desc=eq_class["case"],
                method_name=method_name,
                case_name=eq_class["case"].replace(" ", "_").replace("\"", ""),
                pre=method_data["pre"],
                expected=eq_class["expected"],
                post=method_data["post"],
                act_assert_code=act_assert
            )
        )
    return case_blocks

def render_and_save(spec: Dict[str, Any], config: Dict[str, Any]):
    module_name = spec["module"]
    test_methods = []

    for method in spec["methods"]:
        test_methods.extend(generate_method_tests(method))

    tests_block = "\n".join(test_methods)

    file_content = TEST_FILE_TEMPLATE.format(
        spec_source=config.get("spec_path", "N/A"),
        module_name=module_name,
        test_methods=tests_block
    )

    out_dir = Path(config.get("output_dir", "src/test/java/org/example"))
    out_dir.mkdir(parents=True, exist_ok=True)
    output_file = out_dir / f"{module_name}GeneratedTest.java"

    output_file.write_text(file_content, encoding="utf-8")
    print(f"[✓] Сгенерирован файл: {output_file}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--config", default="config.yaml")
    args = parser.parse_args()

    with open(args.config, "r", encoding="utf-8") as f:
        config = yaml.safe_load(f)

    with open(config["spec_path"], "r", encoding="utf-8") as f:
        spec_data = yaml.safe_load(f)

    render_and_save(spec_data, config)