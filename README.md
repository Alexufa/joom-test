Реализована построчная сортировкa файла.
Собрать jar - gradle jar.

Запуск с 2 аргументами (1 - количество строк, 2 - максимальное количество символов в строке)
создаст в директории /test два файла: input.txt и result.txt
Пример запуска: java -jar -Xmx10m joom-test-1.0-SNAPSHOT.jar 500000 100
(генерируется файл размером 50Mb)

Возможно передача 3 аргументом пути папки, куда запишется входной файл и результат
java -jar -Xmx10m joom-test-1.0-SNAPSHOT.jar 500000 100 c:\tmp
 