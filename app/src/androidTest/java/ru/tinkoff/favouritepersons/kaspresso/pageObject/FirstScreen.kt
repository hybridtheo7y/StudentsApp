package ru.tinkoff.favouritepersons.kaspresso.pageObject

import android.view.View
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher
import ru.tinkoff.favouritepersons.R

class FirstScreen : BaseScreen() {
    val noPersonTitle = KTextView { withId(R.id.tw_no_persons) }
    val addStudentButton = KButton { withId(R.id.fab_add_person) }
    val addStudentManuallyButton = KButton { withId(R.id.fab_add_person_manually) }
    val addStudentByNetworkButton = KButton { withId(R.id.fab_add_person_by_network) }


    val rvPerson = KRecyclerView(
        builder = { withId(R.id.rv_person_list) },
        itemTypeBuilder = {
            itemType(::PersonItem)
        }
    )

    class PersonItem(parent: Matcher<View>) : KRecyclerItem<PersonItem>(parent) {
        val personName = KTextView(parent) { withId(R.id.person_name) }
        val personPrivateInfo = KTextView(parent) { withId(R.id.person_private_info) }
        val personEmail = KTextView(parent) { withId(R.id.person_email) }
        val personPhone = KTextView(parent) { withId(R.id.person_phone) }
        val personAddress = KTextView(parent) { withId(R.id.person_address) }
        val personRating = KTextView(parent) { withId(R.id.person_rating) }
    }

    fun checkRecyclerElements(rvSize: Int) {
        val namesList = listOf("Person First", "Person Second", "Person Third")
        FirstScreen {
            rvPerson {
                isDisplayed()
                hasSize(rvSize)
                for (i in rvSize - 1..0) {
                    childAt<PersonItem>(i) {
                        personName.hasText(namesList[i])
                    }
                }
            }
        }
    }

    fun addStudentByNetworkButtonClick(count: Int) {
        for (i in 0 until count) {
            FirstScreen {
                addStudentByNetworkButton.click()
            }
        }
    }

    companion object {
        inline operator fun invoke(crossinline block: FirstScreen.() -> Unit) =
            FirstScreen().block()
    }
}