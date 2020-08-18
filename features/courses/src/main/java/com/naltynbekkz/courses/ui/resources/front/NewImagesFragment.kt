package com.naltynbekkz.courses.ui.resources.front

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.naltynbekkz.courses.ui.resources.viewmodel.NewResourceViewModel
import com.naltynbekkz.core.Constants.Companion.PERMISSION_REQUEST_CODE
import com.naltynbekkz.core.Constants.Companion.REQUEST_CODE_CHOOSE
import com.naltynbekkz.core.Convert
import com.naltynbekkz.core.ImagesAdapter
import com.naltynbekkz.courses.R
import com.naltynbekkz.courses.databinding.FragmentNewImagesBinding
import com.naltynbekkz.courses.model.Resource
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewImagesFragment : Fragment() {

    private val viewModel: NewResourceViewModel by viewModels()
    private lateinit var adapter: ImagesAdapter
    lateinit var binding: FragmentNewImagesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_images, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.resource = Resource(viewModel.userCourse, "img")

        adapter = ImagesAdapter(::selectImages)

        binding.recyclerView.adapter = adapter

        binding.semesterSpinner.setAdapter(
            ArrayAdapter(
                requireContext(), R.layout.item_spinner_layout, resources.getStringArray(
                    R.array.semesters
                )
            )
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    selectImages()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            adapter.setData(Matisse.obtainResult(data) as ArrayList<Uri>)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                if (adapter.images.size != 0 &&
                    binding.resource!!.semester.isNotEmpty() &&
                    binding.resource!!.title.isNotEmpty()
                ) {
                    binding.loading = true

                    adapter.setState(0)

                    viewModel.post(
                        resource = binding.resource!!.apply {
                            year = binding.yearEditText.text.toString().toLong()
                        },
                        success = findNavController()::navigateUp,
                        failure = fun() {
                            binding.loading = null
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong. Try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        images = adapter.images,
                        compress = fun(uri: Uri): ByteArray {
                            return Convert.compress(uri, requireContext().contentResolver)
                        },
                        done = fun(position: Int) {
                            adapter.setState(position + 1)
                        }
                    )

                } else {
                    binding.loading = false
                }
            }
        }
        return true
    }


    private fun selectImages() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG))
                .countable(true)
                .maxSelectable(5)
                .autoHideToolbarOnSingleTap(true)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE)
        }
    }

}
