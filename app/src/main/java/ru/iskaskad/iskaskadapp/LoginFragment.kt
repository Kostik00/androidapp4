package ru.iskaskad.iskaskadapp


import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.LOGIN_ID
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.RunMode
import ru.iskaskad.iskaskadapp.databinding.FragmentLoginBinding
import java.net.URL


class LoginFragment : Fragment() {
    val LogTAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!



//    private val AppVM: IsKaskadAPPVM by activityViewModels()

    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle

        binding.authButton.setOnClickListener { tryToLogin() }

        arguments?.let {
            if (it.containsKey(ISKaskadAPP.REQUEST_PARAM_RUNMODE) )
                when (it.getInt(ISKaskadAPP.REQUEST_PARAM_RUNMODE)) {
                    R.id.radio_Sklad    ->  binding.radioFindPasp.isChecked   = true
                    R.id.radio_FindPasp ->  binding.radioFindPasp.isChecked    = true
                    R.id.radio_MTask    ->  binding.radioMTask.isChecked       = true
                    R.id.radio_Sklad_outm ->  binding.radioSkladOutm.isChecked = true
                    else  -> { }
                }

        }


        LOGIN_ID = ""

    }
    private fun genLoginID(pair: String):String {
        return Base64.encodeToString(pair.toByteArray(Charsets.UTF_8), Base64.DEFAULT).replace("\n", "")
    }




    private var LoginJob: Job = Job()

    private fun tryToLogin() {
        val login = binding.loginField.text.toString()
        val password = binding.passwordField.text.toString()

//        if (login.isBlank() || password.isBlank()) {
//            Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show()
//            return
//        }

        //TODO GenLoginID
        val TmpLoginID = genLoginID(login + " " + password)
        val URLStr = ISKaskadAPP.makeURLStr(ISKaskadAPP.URL_CHECKPASSWORD, "", TmpLoginID)

        if (LoginJob.isActive) LoginJob.cancel()

        LoginJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY START ID=CheckLogin URLStr=$URLStr")

            try {
                val ResultStr = URL(URLStr).readText()

                ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY COMPLETE ID=CheckLogin")

                if (isActive)
                    launch(Dispatchers.Main) {

                        if (ResultStr == ISKaskadAPP.URL_RESULT_SUCCESS) {

                            LOGIN_ID = TmpLoginID
                            RunMode = binding.RadioGroupRunMode.checkedRadioButtonId
                            Toast.makeText(requireContext(), "Успешная авторизация", Toast.LENGTH_SHORT).show()

                            when (RunMode) {
                                R.id.radio_Sklad,
                                R.id.radio_FindPasp,
                                R.id.radio_Sklad_outm -> {
                                    findNavController().navigate(
                                        R.id.nav_home,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.nav_login, true)
                                            .setLaunchSingleTop(true)
                                            .build()
                                    )
                                }
                                R.id.radio_MTask -> {

                                    findNavController().navigate(
                                        R.id.nav_mtask,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.nav_login, true)
                                            .setLaunchSingleTop(true)
                                            .build()
                                    )

                                }
                                else -> { /* ничего не делать */ }
                            }

                        } else {
                            Toast.makeText(requireContext(), "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY Error ID=CheckLogin URLStr=$URLStr")
                if (isActive)
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка: $e", Toast.LENGTH_LONG).show()
                    }
            }

        }
    }



    override fun onDestroyView() {
        if (LoginJob.isActive)   LoginJob.cancel()
        super.onDestroyView()
        _binding = null
    }
}