package com.example.antivirus.db;

import com.example.antivirus.model.Antivirus;
import com.example.antivirus.model.Criterion;
import com.example.antivirus.model.CriterionOption;
import com.example.antivirus.model.Rule;
import com.example.antivirus.model.RuleCondition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class SeedData {
    private SeedData() {
    }

    public static void seed() throws SQLException {
        AntivirusRepository antivirusRepository = new AntivirusRepository();
        CriterionRepository criterionRepository = new CriterionRepository();
        RuleRepository ruleRepository = new RuleRepository();

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ruleRepository.deleteAll(connection);
                criterionRepository.deleteAll(connection);
                antivirusRepository.deleteAll(connection);

                for (Antivirus item : antiviruses()) {
                    antivirusRepository.save(connection, item);
                }
                for (Criterion criterion : criteria()) {
                    criterionRepository.saveCriterion(connection, criterion);
                }
                for (CriterionOption option : options()) {
                    criterionRepository.saveOption(connection, option);
                }
                for (Rule rule : rules()) {
                    ruleRepository.save(connection, rule);
                }

                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static List<Antivirus> antiviruses() {
        return List.of(
                new Antivirus(
                        "defender",
                        "Microsoft Defender for Business",
                        "Раціональний вибір для Windows-мережі з хмарним адмініструванням.",
                        "Порада експертної системи: обрати це рішення, якщо мережа побудована переважно на Windows, організація використовує екосистему Microsoft, а адміністратору потрібна централізована хмарна консоль без надмірного ускладнення.",
                        true
                ),
                new Antivirus(
                        "eset",
                        "ESET PROTECT Entry",
                        "Підходить для малих і середніх мереж, де важлива швидкодія.",
                        "Порада експертної системи: обрати це рішення, якщо у мережі є слабші комп'ютери, потрібен легкий агент захисту, централізоване керування та стабільна робота без помітного уповільнення робочих станцій.",
                        true
                ),
                new Antivirus(
                        "bitdefender",
                        "Bitdefender GravityZone Business Security",
                        "Добрий баланс між сильним захистом і централізованим керуванням.",
                        "Порада експертної системи: обрати це рішення, якщо основним ризиком є шифрувальники, небезпечні файли, поштові атаки або потрібно контролювати групи пристроїв у середній комп'ютерній мережі.",
                        true
                ),
                new Antivirus(
                        "sophos",
                        "Sophos Intercept X Endpoint",
                        "Вибір для складної інфраструктури з підвищеними вимогами до контролю.",
                        "Порада експертної системи: обрати це рішення, якщо є IT-відділ, сервери, критичні вузли мережі, потреба у розширеному захисті від шифрувальників і можливість виділити більший бюджет.",
                        true
                ),
                new Antivirus(
                        "avast",
                        "Avast Business Antivirus",
                        "Простий варіант для малого офісу з мінімальним бюджетом.",
                        "Порада експертної системи: обрати це рішення, якщо мережа невелика, окремого адміністратора немає, а головним критерієм є проста установка та невисока вартість.",
                        true
                )
        );
    }

    private static List<Criterion> criteria() {
        return List.of(
                new Criterion("scale", "Масштаб мережі", 10, true),
                new Criterion("os", "Основне середовище", 20, true),
                new Criterion("admin", "Як бажано адмініструвати захист", 30, true),
                new Criterion("threat", "Основні ризики", 40, true),
                new Criterion("priority", "Головний пріоритет", 50, true),
                new Criterion("staff", "Хто буде обслуговувати захист", 60, true),
                new Criterion("budget", "Бюджет", 70, true)
        );
    }

    private static List<CriterionOption> options() {
        return List.of(
                option("scale", "home", "1-5 комп'ютерів / домашній або малий офіс", 10),
                option("scale", "small", "6-20 комп'ютерів / невелика організація", 20),
                option("scale", "medium", "21-100 комп'ютерів / середня мережа", 30),
                option("scale", "enterprise", "Понад 100 пристроїв / велика організація", 40),

                option("os", "windows", "Переважно Windows", 10),
                option("os", "mixed", "Змішане середовище: Windows, macOS, Linux", 20),
                option("os", "servers", "Є сервери та критичні вузли мережі", 30),
                option("os", "old_pcs", "Є слабкі або застарілі ПК", 40),

                option("admin", "simple", "Просте встановлення без складного адміністрування", 10),
                option("admin", "cloud", "Хмарна консоль керування", 20),
                option("admin", "central_policy", "Централізовані політики для груп комп'ютерів", 30),
                option("admin", "local_minimal", "Мінімальне втручання у роботу ПК", 40),

                option("threat", "basic", "Звичайні віруси та небажані програми", 10),
                option("threat", "phishing", "Фішинг, небезпечні сайти, поштові загрози", 20),
                option("threat", "ransomware", "Шифрувальники та атаки на файли", 30),
                option("threat", "network", "Мережеві атаки, сервери, складна інфраструктура", 40),

                option("priority", "speed", "Мінімальне навантаження на систему", 10),
                option("priority", "max_protection", "Максимальний рівень захисту", 20),
                option("priority", "integration", "Інтеграція з Microsoft/Windows-середовищем", 30),
                option("priority", "control", "Гнучкий контроль і політики безпеки", 40),
                option("priority", "budget", "Найменша вартість рішення", 50),

                option("staff", "none", "Окремого адміністратора немає", 10),
                option("staff", "one_admin", "Один системний адміністратор", 20),
                option("staff", "security_team", "Є IT-відділ або фахівець з безпеки", 30),

                option("budget", "low", "Мінімальний", 10),
                option("budget", "medium", "Середній", 20),
                option("budget", "high", "Високий, головне - надійність", 30)
        );
    }

    private static List<Rule> rules() {
        return List.of(
                rule("R1", "defender", "Windows + Microsoft-інтеграція", 34,
                        "Для Windows-мережі з пріоритетом інтеграції логічно рекомендувати Microsoft Defender for Business.",
                        cond("os", "windows"), cond("priority", "integration"), cond("admin", "cloud")),
                rule("R2", "defender", "Малий бізнес без окремої команди безпеки", 22,
                        "Якщо окремої команди безпеки немає, краще обирати просте рішення з централізованим керуванням.",
                        cond("scale", "small"), cond("staff", "none"), cond("os", "windows")),
                rule("R3", "eset", "Слабкі ПК + швидкодія", 36,
                        "Для застарілих робочих станцій експерт надає перевагу легшому захисту з меншим навантаженням.",
                        cond("os", "old_pcs"), cond("priority", "speed"), cond("admin", "local_minimal")),
                rule("R4", "eset", "Невелика мережа з централізованим керуванням", 24,
                        "Для малого або середнього офісу потрібен баланс простоти, швидкості й централізованого контролю.",
                        cond("scale", "small"), cond("admin", "central_policy"), cond("priority", "speed")),
                rule("R5", "bitdefender", "Захист від шифрувальників", 35,
                        "Якщо основний ризик - ransomware, експерт обирає рішення з сильним комплексним захистом.",
                        cond("threat", "ransomware"), cond("priority", "max_protection"), cond("budget", "medium")),
                rule("R6", "bitdefender", "Середня мережа + один адміністратор", 28,
                        "Для середньої мережі один адміністратор потребує зрозумілого централізованого керування та сильної автоматизації.",
                        cond("scale", "medium"), cond("staff", "one_admin"), cond("admin", "central_policy")),
                rule("R7", "sophos", "Складна інфраструктура", 38,
                        "Для складних мереж із серверами і командою безпеки експерт радить рішення з розширеним контролем.",
                        cond("scale", "enterprise"), cond("os", "servers"), cond("staff", "security_team")),
                rule("R8", "sophos", "Високі вимоги до контролю", 30,
                        "Якщо головний пріоритет - гнучкий контроль, доцільно радити рішення для розширеного адміністрування.",
                        cond("priority", "control"), cond("admin", "central_policy"), cond("budget", "high")),
                rule("R9", "avast", "Мінімальний бюджет", 30,
                        "Для дуже малого офісу з мінімальним бюджетом експерт може порадити простіше бізнес-рішення.",
                        cond("scale", "home"), cond("budget", "low"), cond("admin", "simple")),
                rule("R10", "avast", "Базовий захист без адміністратора", 24,
                        "Якщо загрози базові, а адміністратора немає, пріоритетом стає проста установка і зрозуміле користування.",
                        cond("staff", "none"), cond("priority", "budget"), cond("threat", "basic")),
                rule("R11", "bitdefender", "Поштові та веб-загрози", 21,
                        "Для фішингу й небезпечних сайтів потрібен комплексний endpoint-захист із контролем політик.",
                        cond("threat", "phishing"), cond("admin", "central_policy"), cond("priority", "max_protection")),
                rule("R12", "eset", "Змішана мережа без надмірної складності", 20,
                        "Для змішаного середовища з обмеженим адмініструванням важливі стабільність і невелике навантаження.",
                        cond("os", "mixed"), cond("priority", "speed"), cond("budget", "medium"))
        );
    }

    private static CriterionOption option(String criterionId, String id, String label, int sortOrder) {
        return new CriterionOption(criterionId, id, label, sortOrder, true);
    }

    private static RuleCondition cond(String criterionId, String optionId) {
        return new RuleCondition(criterionId, optionId, null, null);
    }

    private static Rule rule(String id, String antivirusId, String title, int weight, String explanation, RuleCondition... conditions) {
        return new Rule(id, antivirusId, null, title, weight, explanation, true, List.of(conditions));
    }
}
