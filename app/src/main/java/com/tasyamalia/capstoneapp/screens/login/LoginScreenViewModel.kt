package com.tasyamalia.capstoneapp.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.tasyamalia.capstoneapp.model.MUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel() : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(
                                "LoginScreenViewModel",
                                "signInWithEmailAndPassword success ${task.result}"
                            )
                            home()
                        } else {
                            Log.d(
                                "LoginScreenViewModel",
                                "signInWithEmailAndPassword ${task.result}"
                            )
                        }
                    }

            } catch (ex: Exception) {
                Log.d("LoginScreenViewModel", "signInWithEmailAndPassword $ex")
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName = displayName)
                        home()
                    } else {
                        Log.d(
                            "LoginScreenViewModel",
                            "createUserWithEmailAndPassword ${task.result}"
                        )
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "Life is Great",
            profession = "Android Developer",
            id = null,
        ).toMap()

        FirebaseFirestore.getInstance().collection("users").add(user)
    }


}