package com.l0122075.humamalwi.tubes

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.l0122075.humamalwi.tubes.databinding.ActivityRegisterBinding
import java.util.prefs.Preferences

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var auth : FirebaseAuth
    lateinit var userData: DataUser
    lateinit var pref: preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        pref = preferences(this)

        binding.goLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener{
            val email = binding.edtEmailRegister.text.toString()
            val pass = binding.edtPasswordRegister.text.toString()

            if(email.isEmpty()){
                binding.edtEmailRegister.error = "Isi Email Terlebih Dahulu"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailRegister.error = "Email Tidak Valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if(pass.isEmpty()){
                binding.edtPasswordRegister.error = "Isi Password Terlebih Dahulu"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if(pass.length < 6){
                binding.edtPasswordRegister.error = "Password Minimal 6 Karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, pass)
        }
    }

    private fun RegisterFirebase(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {

                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        pref.prefUserId = userId
                        addUserDataToDatabase(userId, email)
                    } else {
                        Toast.makeText(this, "Gagal mendapatkan ID pengguna", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Gagal Daftar: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserDataToDatabase(userId: String, email: String) {

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        userData = DataUser(userId,null ,email, "Nama", 0, "+012345679")
        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil Daftar dan Menyimpan Data", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}