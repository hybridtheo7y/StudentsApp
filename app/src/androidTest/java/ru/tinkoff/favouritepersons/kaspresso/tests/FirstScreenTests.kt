package ru.tinkoff.favouritepersons.kaspresso.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.kaspresso.kaspressoBuilder
import ru.tinkoff.favouritepersons.kaspresso.pageObject.FirstScreen
import ru.tinkoff.favouritepersons.kaspresso.rules.LocalhostPreferenceRule
import ru.tinkoff.favouritepersons.kaspresso.utils.fileToString
import ru.tinkoff.favouritepersons.presentation.activities.MainActivity
import ru.tinkoff.favouritepersons.room.PersonDataBase

class FirstScreenTests : TestCase(kaspressoBuilder) {

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


    // Case1
    @Test
    fun noPersonDataTest() = run {
        stubFor(
            get(urlPathMatching("/api/"))
                .willReturn(ok(fileToString("mock/person1.json")))
        )
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
                addStudentByNetworkButton.click()
            }
            step("Проверяем что добавился студент") {
                rvPerson {
                    isVisible()
                    hasSize(1)
                    childAt<FirstScreen.PersonItem>(0) {
                        personName.hasText("Person First")
                    }
                }
            }
            step("Проверяем, что надпись об отсутствии не отображается") {
                noPersonTitle.isNotDisplayed()
            }
        }
    }

    // Case2
    @Test
    fun personDeleteTest() = run {
        val personCount = 3
        val personCountAfterOneDeleted = 2
        fewStudentsStubbing()
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
            step("Удаляем второго студента") {
                rvPerson {
                    childAt<FirstScreen.PersonItem>(1) {
                        personName.hasText("Person Second")
                        view.perform(ViewActions.swipeLeft())
                    }
                }
            }
            step("Проверяем, что студент удален") {
                checkRecyclerElements(personCountAfterOneDeleted)
            }
        }
    }

    // Case3
    @Test
    fun bottomSheetTest() = run {
        val personCount = 3
        fewStudentsStubbing()
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
            step("Жмем на сортировку") {
                clickSortButton()
            }
            step("Проверяем дефолтное состояние боттомшита") {
                checkDefaultRadioButtonsState()
            }
        }
    }

    // Case4
    @Test
    fun ageSortTest() = run {
        val personCount = 3
        fewStudentsStubbing()
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
            step("Добавляем 3 студента из сети") {
                addStudentByNetworkButtonClick(personCount)
            }
            step("Проверяем, что надпись об отсутствии не отображается") {
                noPersonTitle.isNotDisplayed()
            }
            step("Проверяем что добавилось 3 студента") {
                checkRecyclerElements(personCount)
            }
            step("Сортируем по возрасту") {
                clickSortButton()
                sortByAge()
            }
            step("Проверяем, что список персон отсортирован по убыванию возраста") {
                rvPerson {
                    hasSize(3)
                    childAt<FirstScreen.PersonItem>(0) {
                        personPrivateInfo.hasText("Male, 82")
                    }
                    childAt<FirstScreen.PersonItem>(1) {
                        personPrivateInfo.hasText("Male, 72")
                    }
                    childAt<FirstScreen.PersonItem>(2) {
                        personPrivateInfo.hasText("Male, 22")
                    }
                }
            }
        }
    }

    // Case10
    @Test
    fun internetConnectionErrorTest() = run {
        stubFor(
            get(urlPathMatching("/api/"))
                .willReturn(ok(fileToString("mock/empty.json")))
        )
        FirstScreen {
            step("Жмем на добавление пользователя из сети") {
                addStudentButton.click()
                addStudentByNetworkButton.click()
            }
            step("Проверяем, что появилось сообщение об ошибке интернет соединения") {
                checkSnackBarText()
            }
        }
    }

    private fun checkDefaultRadioButtonsState() {
        onView(withId(R.id.bsd_rb_default))
            .check(matches(withText(containsString("По умолчанию (В порядке добавления)"))))
            .check(matches(isChecked()))
        onView(withId(R.id.bsd_rb_age))
            .check(matches(withText(containsString("По возрасту"))))
            .check(matches(isNotChecked()))
        onView(withId(R.id.bsd_rb_rating))
            .check(matches(withText(containsString("По итоговому баллу"))))
            .check(matches(isNotChecked()))
        onView(withId(R.id.bsd_rb_name))
            .check(matches(withText(containsString("По ФИО"))))
            .check(matches(isNotChecked()))
    }

    private fun sortByAge() {
        onView(withId(R.id.bsd_rb_age)).perform(click())
    }

    private fun clickSortButton() {
        onView(withId(R.id.action_item_sort)).perform(click())
    }

    private fun checkSnackBarText() {
        onView(withText("Internet error! Check your connection")).check(matches(isDisplayed()))
    }

    private fun fewStudentsStubbing() {
        stubFor(
            get(urlPathMatching("/api/"))
                .inScenario("AgeSort")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("Person1")
                .willReturn(ok(fileToString("mock/person1.json")))
        )
        stubFor(
            get(urlPathMatching("/api/"))
                .inScenario("AgeSort")
                .whenScenarioStateIs("Person1")
                .willSetStateTo("Person2")
                .willReturn(ok(fileToString("mock/person2.json")))
        )
        stubFor(
            get(urlPathMatching("/api/"))
                .inScenario("AgeSort")
                .whenScenarioStateIs("Person2")
                .willSetStateTo("Person3")
                .willReturn(ok(fileToString("mock/person3.json")))
        )
    }

}