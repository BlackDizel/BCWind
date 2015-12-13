# BCWind
test app for display weather for selected cities.

to build, create keys.xml with openweathermap_key item with app id, obtained by register on openweathermap.org

In project used openweathearmap api (http://openweathermap.org/API), asynctasks, DeafultHttpClient, local files, json parser, actionbar search.

In real project need to use, for example, retrofit instead of DefaultHttpClient or clean OkHttp library.

==================

Приложение с отображением погоды для выбранных городов.

Для запуска проекта необходимо добавить файл keys.xml в ресурсы с элементом openweathermap_key и значением app id, полученным при регистрации на openweathermap.org.

В проекте реализованы 
openweathermap api, 
асихронные задачи, 
DefaultHttpClient, 
работа с локальными файлами, 
парсинг json, 
actionbar поиск. 
Также файлы разложены согласно MVC паттерну.

В реальном проекте необходимо использовать библиотеку retrofit для получения и парсинга данных или OkHttp (только для получения данных) вместо DefaultHttpClient, т.к. последний считается устаревшим и в проектах для android 6.x работать из коробки не будет.
