package com.bignerdranch.android.mysololife.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.mysololife.MainActivity
import com.bignerdranch.android.mysololife.R
import com.bignerdranch.android.mysololife.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_join)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_join)
        binding.joinBtn.setOnClickListener {
            var isGoToJoin = true


            val email = binding.emailArea.text.toString()
            val password = binding.passwordArea.text.toString()
            val passwordCheck = binding.passwordCheckArea.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            else if(password.isEmpty()){
                Toast.makeText(this,"패스워드를 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            else if(passwordCheck.isEmpty()){
                Toast.makeText(this,"패스워드 확인란을 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            else if(!password.equals(passwordCheck)){
                Toast.makeText(this,"비밀번호가 다릅니다.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            else if(password.length<6){
                Toast.makeText(this,"비밀번호를 6자리 이상으로 입력해주세요.",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(isGoToJoin){

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"성공", Toast.LENGTH_LONG).show()

                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else {
                        Toast.makeText(this,"실패", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}