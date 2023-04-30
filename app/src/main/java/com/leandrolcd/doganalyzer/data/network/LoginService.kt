package com.leandrolcd.doganalyzer.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class LoginService @Inject constructor( private val firebaseAuth: FirebaseAuth
) {

    fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)

    }
    fun getUser(): FirebaseUser? {
        return  firebaseAuth.currentUser
    }
    fun signInWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }
    fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    fun forgotPassword(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    fun signOut() {
       firebaseAuth.signOut()
    }
}