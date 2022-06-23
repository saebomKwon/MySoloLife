package com.bignerdranch.android.mysololife.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.mysololife.R
import com.bignerdranch.android.mysololife.comment.CommentLVAdapter
import com.bignerdranch.android.mysololife.comment.CommentModel
import com.bignerdranch.android.mysololife.databinding.ActivityBoardInsideBinding
import com.bignerdranch.android.mysololife.utils.FBAuth
import com.bignerdranch.android.mysololife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding : ActivityBoardInsideBinding
    private lateinit var key:String
    private val commentDataList = mutableListOf<CommentModel>()
    private lateinit var commentAdapter : CommentLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_inside)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_board_inside)
        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }

        key = intent.getStringExtra("key").toString()
        getBoardData(key)

        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }

        getCommentData(key)

        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter

    }
    fun getCommentData(key:String){
        val postListener = object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){

            commentDataList.clear()

            for (dataModel in dataSnapshot.children){
                val item = dataModel.getValue(CommentModel::class.java)
                commentDataList.add(item!!)
            }
            commentAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG,"loadPost:onCancelled",databaseError.toException())
        }
    }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }


    fun insertComment(key : String){
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime()
                )
            )
        Toast.makeText(this,"댓글 입력 완료",Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")

    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        val alertDialog=mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {

            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key",key)
            startActivity(intent)
        }
        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this,"삭제완료",Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun getBoardData(key:String){
        val postListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){

                try{
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    if(dataModel!=null){
                        binding.titleArea.text = dataModel!!.title
                        binding.textArea.text=dataModel!!.content
                        binding.timeArea.text=dataModel!!.time

                        val myUid = FBAuth.getUid()
                        val writerUid = dataModel.uid

                        if(myUid.equals(writerUid)){
                            binding.boardSettingIcon.isVisible = true
                        }else{

                        }
                    }
                }catch (e:Exception){
                    Log.d(TAG,"삭제완료")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
    FBRef.boardRef.child(key).addValueEventListener(postListener)


    }
}