package ru.tinkoff.favouritepersons.presentation.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.presentation.PersonFields
import ru.tinkoff.favouritepersons.presentation.rv_helpers.PersonListAdapter
import ru.tinkoff.favouritepersons.presentation.rv_helpers.SwipeToDeleteCallback
import ru.tinkoff.favouritepersons.presentation.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var vieModel: MainViewModel
    private lateinit var adapter : PersonListAdapter

    lateinit var fab_add_person_manually : FloatingActionButton
    lateinit var fab_add_person_from_network : FloatingActionButton
    lateinit var progressIndicator : LinearProgressIndicator

    lateinit var tw_no_persons : TextView

    var isFABOpen : Boolean = IS_FAB_MENU_OPENED_BY_DEFAULT

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_item_sort -> showBottomSheet()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivityLifecycle","onCreate")
        setContentView(R.layout.activity_main)
        setUpRecycler()

        fab_add_person_manually = findViewById(R.id.fab_add_person_manually)
        fab_add_person_from_network = findViewById(R.id.fab_add_person_by_network)
        progressIndicator= findViewById(R.id.progress_indicator)
        tw_no_persons = findViewById(R.id.tw_no_persons)

        vieModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[MainViewModel::class.java]

        vieModel.personList.observe(this) {

            adapter.submitList(it.toMutableList())
            if (it.isNullOrEmpty())
                tw_no_persons.visibility = View.VISIBLE
            else
                tw_no_persons.visibility = View.INVISIBLE
        }


        vieModel.usersLoadError.observe(this){
            if (!it.isNullOrEmpty())
                Snackbar.make(fab_add_person_from_network, "Internet error! Check your connection", Snackbar.LENGTH_INDEFINITE).show()
        }

        vieModel.isLoading.observe(this){
            if (it) {
                progressIndicator.visibility = View.VISIBLE
            } else
                progressIndicator.visibility = View.GONE
        }
        initializeFabClickListeners()

    }


    fun initializeFabClickListeners(){

        findViewById<FloatingActionButton>(R.id.fab_add_person).setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        fab_add_person_manually.setOnClickListener{
            val intent = PersonItemActivity.newIntentAddItem(this)
            startActivity(intent)
        }

        fab_add_person_from_network.setOnClickListener {
            vieModel.getRandomPers()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        fab_add_person_manually.animate().translationY(-resources.getDimension(R.dimen.standard_75))
        fab_add_person_from_network.animate().translationY(-resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fab_add_person_manually.animate().translationY(0f)
        fab_add_person_from_network.animate().translationY(0f)
    }

    private fun setUpRecycler() {
        val rvPersonList = findViewById<RecyclerView>(R.id.rv_person_list)
        rvPersonList.layoutManager = LinearLayoutManager(this)

        adapter = PersonListAdapter(PersonListAdapter.OnClickListener {
            val intent = PersonItemActivity.newIntentEditItem(this, it.id!!)
            startActivity(intent)
        }
        )

        adapter.registerAdapterDataObserver( object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                    rvPersonList.scrollToPosition(positionStart)
            }
        })

        rvPersonList.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvPersonList.adapter as PersonListAdapter
                val item = adapter.currentList[viewHolder.adapterPosition]
                vieModel.deletePersonItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvPersonList)
    }


    private fun showBottomSheet(){
        val dialog = BottomSheetDialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)


        val rb_def = bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_default)
        val rb_age = bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_age)
        val rb_rating = bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_rating)
        val rb_name = bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_name)

        when(vieModel.currentSortMethod.value){
            PersonFields.NO_METHOD -> rb_def.isChecked = true
            PersonFields.BY_AGE -> rb_age.isChecked = true
            PersonFields.BY_SURNAME -> rb_name.isChecked = true
            PersonFields.BY_RATING -> rb_rating.isChecked = true
            else -> {
                rb_def.isChecked = true
            }
        }

        bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_default).setOnClickListener {
            vieModel.setSortMethod(PersonFields.NO_METHOD)
            dialog.dismiss()
        }
        bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_age).setOnClickListener {
            vieModel.setSortMethod(PersonFields.BY_AGE)
            dialog.dismiss()
        }
        bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_rating).setOnClickListener {
            vieModel.setSortMethod(PersonFields.BY_RATING)
            dialog.dismiss()
        }
        bottomSheet.findViewById<RadioButton>(R.id.bsd_rb_name).setOnClickListener {
            vieModel.setSortMethod(PersonFields.BY_SURNAME)
            dialog.dismiss()
        }

        with(dialog) {
            setContentView(bottomSheet)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes?.windowAnimations = R.style.DialogSheetAnimation
            show()
        }
    }

    companion object {
        const val IS_FAB_MENU_OPENED_BY_DEFAULT = false
    }

}