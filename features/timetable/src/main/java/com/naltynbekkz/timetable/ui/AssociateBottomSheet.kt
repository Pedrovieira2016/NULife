package com.naltynbekkz.timetable.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naltynbekkz.timetable.adapter.AssociatesAdapter
import com.naltynbekkz.timetable.databinding.BottomSheetAssociateBinding
import com.naltynbekkz.timetable.model.Associate
import java.util.*
import kotlin.collections.ArrayList

class AssociateBottomSheet(
    private val click: (Associate) -> Unit
) : BottomSheetDialogFragment() {

    var userCourses: ArrayList<Associate>? = null
    var userClubs: ArrayList<Associate>? = null
    var routines: List<com.naltynbekkz.timetable.model.Occurrence>? = null

    lateinit var binding: BottomSheetAssociateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetAssociateBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = AssociatesAdapter(
            associates = ArrayList(TreeMap<String, Associate>().apply {
                userCourses?.forEach {
                    this[it.id] = it
                }
                userClubs?.forEach {
                    this[it.id] = it
                }
                Associate.getData(routines).forEach {
                    this[it.id] = it
                }
            }.values),
            click = fun(associate: Associate) {
                click(associate)
                dismiss()
            }
        )

        return binding.root
    }

}