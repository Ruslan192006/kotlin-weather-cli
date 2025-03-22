import kotlin.random.Random

data class Weather(
    val city: String,
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int
)

sealed class WeatherResult {
    data class Success(val weather: Weather) : WeatherResult()
    data class Error(val message: String) : WeatherResult()
}

class WeatherService {
    private val conditions = listOf("Солнечно", "Облачно", "Дождь", "Снег", "Туман")
    private val cities = mapOf(
        "москва" to "Москва",
        "санкт-петербург" to "Санкт-Петербург",
        "новосибирск" to "Новосибирск",
        "екатеринбург" to "Екатеринбург",
        "казань" to "Казань"
    )

    fun getWeather(cityInput: String): WeatherResult {
        val cityKey = cityInput.lowercase().trim()
        val cityName = cities[cityKey]
        
        return if (cityName != null) {
            WeatherResult.Success(
                Weather(
                    city = cityName,
                    temperature = Random.nextInt(-20, 35),
                    condition = conditions.random(),
                    humidity = Random.nextInt(30, 90),
                    windSpeed = Random.nextInt(0, 25)
                )
            )
        } else {
            WeatherResult.Error("Город не найден")
        }
    }

    fun getForecast(cityInput: String, days: Int): List<Weather> {
        val cityKey = cityInput.lowercase().trim()
        val cityName = cities[cityKey] ?: return emptyList()
        
        return List(days) { day ->
            Weather(
                city = "$cityName (День ${day + 1})",
                temperature = Random.nextInt(-20, 35),
                condition = conditions.random(),
                humidity = Random.nextInt(30, 90),
                windSpeed = Random.nextInt(0, 25)
            )
        }
    }

    fun getAvailableCities(): List<String> = cities.values.toList()
}

fun displayWeather(weather: Weather) {
    println("\n╔════════════════════════════════╗")
    println("║  Погода в ${weather.city.padEnd(18)}║")
    println("╠════════════════════════════════╣")
    println("║  Температура: ${weather.temperature}°C".padEnd(33) + "║")
    println("║  Условия: ${weather.condition}".padEnd(33) + "║")
    println("║  Влажность: ${weather.humidity}%".padEnd(33) + "║")
    println("║  Ветер: ${weather.windSpeed} м/с".padEnd(33) + "║")
    println("╚════════════════════════════════╝\n")
}

fun main() {
    val weatherService = WeatherService()
    val history = mutableListOf<String>()
    
    println("=== Kotlin Weather CLI ===\n")
    
    while (true) {
        println("Выберите действие:")
        println("1. Узнать погоду")
        println("2. Прогноз на несколько дней")
        println("3. Показать доступные города")
        println("4. История запросов")
        println("5. Выход")
        print("\nВаш выбор: ")
        
        when (readLine()?.toIntOrNull()) {
            1 -> {
                print("Введите название города: ")
                val city = readLine() ?: ""
                
                when (val result = weatherService.getWeather(city)) {
                    is WeatherResult.Success -> {
                        displayWeather(result.weather)
                        history.add("Погода в ${result.weather.city}")
                    }
                    is WeatherResult.Error -> println("✗ ${result.message}")
                }
            }
            2 -> {
                print("Введите название города: ")
                val city = readLine() ?: ""
                print("На сколько дней (1-7): ")
                val days = readLine()?.toIntOrNull()?.coerceIn(1, 7) ?: 3
                
                val forecast = weatherService.getForecast(city, days)
                if (forecast.isNotEmpty()) {
                    println("\n=== Прогноз на $days дней ===")
                    forecast.forEach { displayWeather(it) }
                    history.add("Прогноз для $city на $days дней")
                } else {
                    println("✗ Город не найден")
                }
            }
            3 -> {
                println("\nДоступные города:")
                weatherService.getAvailableCities().forEach { println("  • $it") }
                println()
            }
            4 -> {
                if (history.isEmpty()) {
                    println("\nИстория пуста\n")
                } else {
                    println("\n=== История запросов ===")
                    history.forEachIndexed { index, item ->
                        println("${index + 1}. $item")
                    }
                    println()
                }
            }
            5 -> {
                println("До свидания!")
                break
            }
            else -> println("✗ Неверный выбор\n")
        }
    }
}
