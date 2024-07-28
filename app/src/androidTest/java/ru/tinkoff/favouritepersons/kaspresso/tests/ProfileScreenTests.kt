package ru.tinkoff.favouritepersons.kaspresso.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.kaspresso.kaspressoBuilder
import ru.tinkoff.favouritepersons.kaspresso.pageObject.FirstScreen
import ru.tinkoff.favouritepersons.kaspresso.pageObject.ProfileScreen
import ru.tinkoff.favouritepersons.kaspresso.rules.LocalhostPreferenceRule
import ru.tinkoff.favouritepersons.kaspresso.utils.fileToString
import ru.tinkoff.favouritepersons.presentation.activities.MainActivity
import ru.tinkoff.favouritepersons.room.PersonDataBase

class ProfileScreenTests : TestCase(kaspressoBuilder) {

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LocalhostPreferenceRule())
        .around(WireMockRule(5000))
        .around(ActivityScenarioRule(MainActivity::class.java))
    private lateinit var db: PersonDataBase

    @Before
    fun createDb() {
        db = PersonDataBase.getDBClient(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun clearDB() {
        db.personsDao().clearTable()
    }

    // Case5
    @Test
    fun profileScreenTest() = run {
        val personCount = 3
        stubForTests()
        FirstScreen {
            step("Проверяем отображение надписи о отсутствии стундентов") {
                noPersonTitle.isDisplayed()
                noPersonTitle.hasText(R.string.start_screen_no_persons_text)
            }
            step("Нажимаем на кнопку добавления нового студента") {
                addStudentButton.click()
                addStudentManuallyButton.isDisplayed()
                addStudentByNetworkButton.isDisplayed()
            }
            step("Добавляем студента из сети") {
                addStudentByNetworkButtonClick(personCount)
            }
            step("Проверяем, что надпись об отсутствии не отображается") {
                noPersonTitle.isNotDisplayed()
            }
            step("Проверяем что добавилось 3 студента") {
                checkRecyclerElements(personCount)
            }
            step("Нажимаем на 3 студента") {
                rvPerson {
                    childAt<FirstScreen.PersonItem>(2) {
                        personName.click()
                    }
                }
            }
        }
        ProfileScreen {
            step("Проверяем данные студента") {
                personScreenTitle {
                    isDisplayed()
                    hasText(R.string.person_layout_titile_edit)
                }
                placeholderName {
                    isDisplayed()
                    hasHint(R.string.name)
                }
                etName {
                    hasText("Person")
                }
                placeholderSurname {
                    isDisplayed()
                    hasHint(R.string.surname)
                }
                etSurname {
                    isDisplayed()
                    hasText("First")
                }
                placeholderGender {
                    isDisplayed()
                    hasHint(R.string.gender_hint)
                }
                etGender {
                    isDisplayed()
                    hasText("М")
                }
                placeholderBirthdate {
                    isDisplayed()
                    hasHint(R.string.birthdate_hint)
                }
                etBirthdate {
                    isDisplayed()
                    hasText("1952-02-01")
                }
            }
        }
    }

    // Case6
    @Test
    fun profileEditTest() = run {
        val personCount = 3
        stubForTests()
        FirstScreen {
            step("Проверяем отображение надписи о отсутствии стундентов") {
                noPersonTitle.isDisplayed()
                noPersonTitle.hasText(R.string.start_screen_no_persons_text)
            }
            step("Нажимаем на кнопку добавления нового студента") {
                addStudentButton.click()
                addStudentManuallyButton.isDisplayed()
                addStudentByNetworkButton.isDisplayed()
            }
            step("Добавляем студента из сети") {
                addStudentByNetworkButtonClick(personCount)
            }
            step("Проверяем, что надпись об отсутствии не отображается") {
                noPersonTitle.isNotDisplayed()
            }
            step("Проверяем что добавилось 3 студента") {
                checkRecyclerElements(personCount)
            }
            step("Нажимаем на 3 студента") {
                rvPerson {
                    childAt<FirstScreen.PersonItem>(2) {
                        personName.click()
                    }
                }
            }
        }
        ProfileScreen {
            personScreenTitle {
                isDisplayed()
                hasText(R.string.person_layout_titile_edit)
            }
            placeholderName {
                isDisplayed()
                hasHint(R.string.name)
            }
            etName {
                hasText("Person")
                replaceText("Иосиф")
                hasText("Иосиф")
            }
            saveButton.click()
        }
        FirstScreen {
            rvPerson {
                childAt<FirstScreen.PersonItem>(2) {
                    personName.hasText("Иосиф First")
                }
            }
        }
    }

    // Case7
    @Test
    fun userManualAdd() = run {
        val personCount = 1
        val name = "Name"
        val surname = "Surname"
        val gender = "F"
        val birthdate = "2012-12-12"
        val email = "person@mail.com"
        val phone = "+79999999"
        val address = "Ne dom i ne ulitsa"
        val imageLink = "https://miro.medium.com/v2/resize:fit:780/1*_ZypyR9GBteF0CSWyuUPHw.jpeg"
        val score = "99"
        stubForTests()
        FirstScreen {
            step("Проверяем отображение надписи о отсутствии стундентов") {
                noPersonTitle.isDisplayed()
                noPersonTitle.hasText(R.string.start_screen_no_persons_text)
            }
            step("Нажимаем на кнопку добавления нового студента") {
                addStudentButton.click()
                addStudentManuallyButton.isDisplayed()
                addStudentByNetworkButton.isDisplayed()
            }
            step("Добавляем студента из сети") {
                addStudentByNetworkButtonClick(personCount)
            }
            step("Проверяем, что надпись об отсутствии не отображается") {
                noPersonTitle.isNotDisplayed()
            }
            step("Проверяем что добавился 1 студент") {
                checkRecyclerElements(personCount)
            }
            step("Жмем на ручное добавление пользователя") {
                addStudentManuallyButton.click()
            }
        }
        step("Заполняем и сохраняем данные пользователя") {
            ProfileScreen().checkProfileScreenTitile()
            ProfileScreen().typeAndCheckName(name)
            ProfileScreen().typeAndCheckSurname(surname)
            ProfileScreen().typeAndCheckGender(gender)
            ProfileScreen().typeAndCheckBirthdate(birthdate)
            ProfileScreen().typeAndCheckEmail(email)
            ProfileScreen().typeAndCheckPhone(phone)
            ProfileScreen().typeAndCheckAddress(address)
            ProfileScreen().typeAndCheckImageLink(imageLink)
            ProfileScreen().typeAndCheckScore(score)
            ProfileScreen().buttonSaveClick()
        }
        step("Проверяем, что пользователь добавлен") {
            FirstScreen {
                rvPerson {
                    hasSize(2)
                    childAt<FirstScreen.PersonItem>(0) {
                        personName.hasText("Name Surname")
                        personPrivateInfo.hasText("Female, 11")
                        personEmail.hasText("person@mail.com")
                        personPhone.hasText("+79999999")
                        personAddress.hasText("Ne dom i ne ulitsa")
                        personRating.hasText("99")
                    }
                    childAt<FirstScreen.PersonItem>(1) {
                        personName.hasText("Person First")
                    }
                }
            }
        }
    }

    // Case8
    @Test
    fun genderErrorTest() = run {
        FirstScreen {
            step("Добавляем пользователя вручную") {
                addStudentButton.click()
                addStudentManuallyButton.click()
            }
        }
        ProfileScreen {
            step("Нажимаем сохранить и проверяем, что отобразилась ошибка") {
                saveButton.click()
                placeholderGender {
                    hasHint(R.string.gender_hint)
                    hasError("Поле должно быть заполнено буквами М или Ж")
                }
            }
        }
    }

    // Case9
    @Test
    fun wrongDataAtFieldGenderTest() = run {
        val name = "Name"
        val surname = "Surname"
        val gender = "Queer"
        val birthdate = "2012-12-12"
        val email = "person@mail.com"
        val phone = "+79999999"
        val address = "Ne dom i ne ulitsa"
        val imageLink = "https://miro.medium.com/v2/resize:fit:780/1*_ZypyR9GBteF0CSWyuUPHw.jpeg"
        val score = "99"
        FirstScreen {
            step("Жмем на ручное добавление пользователя") {
                addStudentButton.click()
                addStudentManuallyButton.click()
            }
        }
        ProfileScreen().checkProfileScreenTitile()
        ProfileScreen().typeAndCheckName(name)
        ProfileScreen().typeAndCheckSurname(surname)
        ProfileScreen().typeAndCheckGender(gender)
        ProfileScreen().typeAndCheckBirthdate(birthdate)
        ProfileScreen().typeAndCheckEmail(email)
        ProfileScreen().typeAndCheckPhone(phone)
        ProfileScreen().typeAndCheckAddress(address)
        ProfileScreen().typeAndCheckImageLink(imageLink)
        ProfileScreen().typeAndCheckScore(score)
        ProfileScreen().buttonSaveClick()
        ProfileScreen {
            placeholderGender.hasError("Поле должно быть заполнено буквами М или Ж")
            etGender {
                clearText()
                hasText("")
            }
            placeholderGender.hasNoError()
        }
    }


    private fun stubForTests() {
        stubFor(
            get(WireMock.urlPathMatching("/api/"))
                .inScenario("Swipe")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("Person1")
                .willReturn(ok(fileToString("mock/person1.json")))
        )
        stubFor(
            get(WireMock.urlPathMatching("/api/"))
                .inScenario("Swipe")
                .whenScenarioStateIs("Person1")
                .willSetStateTo("Person2")
                .willReturn(ok(fileToString("mock/person2.json")))
        )
        stubFor(
            get(WireMock.urlPathMatching("/api/"))
                .inScenario("Swipe")
                .whenScenarioStateIs("Person2")
                .willSetStateTo("Person3")
                .willReturn(ok(fileToString("mock/person3.json")))
        )
    }
}