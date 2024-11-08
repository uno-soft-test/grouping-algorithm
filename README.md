# Результаты
```
Количество групп с более чем одним элементом: 1910 (или 471, если я неправильно понял формат входных данных)
Время выполнения программы: 10,569 секунд
Использованная память: 0,233 ГБ
```

# Grouping Algorithm

Приложение на Java для группировки данных из CSV файлов на основе совпадений значений в одних и тех же колонках. Проект поддерживает как обычные текстовые файлы (`.txt`), так и сжатые файлы (`.txt.gz`), и может использовать пути по умолчанию или заданные пользователем. Поддерживается логгирование.

---

# Запуск приложения

### Использование файла конфигурации по умолчанию

Если вы хотите использовать файл для чтения, указанный в `application.yml`, выполните:

```bash
java -Xmx1G -jar grouping-algorithm.jar
```

> **Примечание**: Путь к файлу по умолчанию указан в `application.yml`, значения из него берутся для класса конфигурации.
---

### Использование собственного файла

Вы также можете указать свой собственный файл (обычный текстовый или сжатый) в качестве аргумента:

1. **Для обычного текстового файла `.txt`**:

   ```bash
   java -Xmx1G -jar grouping-algorithm.jar путь/к/вашему_файлу.txt
   ```

2. **Для сжатого файла `.txt.gz`**:

   ```bash
   java -Xmx1G -jar grouping-algorithm.jar путь/к/вашему_файлу.txt.gz
   ```
