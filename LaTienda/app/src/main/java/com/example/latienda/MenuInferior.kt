package com.example.latienda

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuInferior.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuInferior : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }//Final de arguments?.let

    }//Final de fun onCreate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        /* ********Código para los botones del menú inferior que está en el fragment_menu_inferior********** */
        val view: View = inflater.inflate(R.layout.fragment_menu_inferior, container, false)

        val botonPerfil = view.findViewById<View>(R.id.Menu_Inferior_Button_Profile) as Button
        botonPerfil.setOnClickListener {
            val intent = Intent(activity, UserProfile::class.java)
            startActivity(intent)
        }

        val botonStore = view.findViewById<View>(R.id.Menu_Inferior_Button_Store) as Button
        botonStore.setOnClickListener {
            val intent = Intent(activity, InfoTienda::class.java)
            startActivity(intent)
        }

        val botonHome = view.findViewById<View>(R.id.Menu_Inferior_Button_Home) as Button
        botonHome.setOnClickListener {
            val intent = Intent(activity, EditProfile::class.java)
            startActivity(intent)
        }
        /* ********Código para los botones del menú inferior que está en el fragment_menu_inferior********** */
        return view


    }//Final de fun onCreateView

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuInferior.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuInferior().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }//Final de Bundle().apply
            }//Final de MenuInferior().apply
    }//Final de companion object



}//Final de la Clase