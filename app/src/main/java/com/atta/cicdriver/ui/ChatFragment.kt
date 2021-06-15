package com.atta.cicdriver.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicdriver.*
import com.atta.cicdriver.adapter.MessagesAdapter
import com.atta.cicdriver.adapter.SamplesAdapter
import com.atta.cicdriver.databinding.FragmentChatBinding
import com.atta.cicdriver.model.ChatChannel
import com.atta.cicdriver.model.Message
import com.atta.cicdriver.model.MessageSample
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val TAG = "ChatActivity"

    private lateinit var db: FirebaseFirestore

    private lateinit var currentChannelId: String

    var messages: ArrayList<Message> = ArrayList()
    var messageSamples: ArrayList<MessageSample> = ArrayList()
    private lateinit var otherUserId: String
    private lateinit var otherUserName: String
    private lateinit var userId: String

    lateinit var messagesAdapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root

        otherUserId = arguments?.let { ChatFragmentArgs.fromBundle(it).userId }!!
        otherUserName = arguments?.let { ChatFragmentArgs.fromBundle(it).userName }!!
        userId = context?.let { SessionManager.with(it).getUserId() }!!


        db = FirebaseFirestore.getInstance()

        getOrCreateChatChannel()

        return view
    }

    private fun getOrCreateChatChannel(){
        db.collection("Drivers").document(userId)
                .collection("EngagedChatChannels")
                .document(otherUserId)
                .get()
                .addOnSuccessListener {
                    if (it.exists()){
                        currentChannelId = it["channelId"] as String

                    }else{

                        val newChannel =  db.collection("ChatChannels").document()

                        newChannel.set(ChatChannel(mutableListOf(userId, otherUserId)))

                        db.collection("Drivers").document(userId)
                                .collection("EngagedChatChannels")
                                .document(otherUserId)
                                .set(mapOf("channelId" to newChannel.id))

                        db.collection("Users").document(otherUserId)
                                .collection("EngagedChatChannels")
                                .document(userId)
                                .set(mapOf("channelId" to newChannel.id))

                        currentChannelId = newChannel.id
                    }

                    getMessages()

                    val userName = context?.let { it1 -> SessionManager.with(it1).getUserName() }

                    binding.sendImg.setOnClickListener {
                        val msg = Message(binding.editTextMessage.text.toString(),
                                Timestamp(Calendar.getInstance().time), userId, otherUserId,
                                userName!!, "to user")

                        binding.editTextMessage.setText("")

                        sendMessage(msg)
                    }

                }
    }

    private fun getMessages(){
        db.collection("ChatChannels").document(currentChannelId)
                .collection("messages")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){

                            messages.add(document.toObject(Message::class.java))

                            //addRoute(route)
                        }
                        //checklists = it.toObjects<Checklist>()
                        showRecycler()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

        getSamples()
    }

    private fun getSamples(){
        db.collection("driver_message_samples")
                .orderBy("order")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){

                            val message = document.toObject(MessageSample::class.java)
                            messageSamples.add(message)

                            //addRoute(route)
                        }
                        showSampleRecycler()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
    }

    private fun addChatMessagesListener() {

        val docRef = db.collection("ChatChannels").document(currentChannelId).collection("messages")
                .orderBy("time")

        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                return@addSnapshotListener
            }
            for (dc in querySnapshot!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        messages.add(dc.document.toObject(Message::class.java))
                        messagesAdapter.notifyDataSetChanged()
                    }

                }
            }


            binding.recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)
            /*            val messages = mutableListOf<Message>()
                        querySnapshot.documents.forEach {
                            messages.add(it.toObject(Message::class.java)!!)
                        }
    */
            //updateRecyclerView(messages)

        }
    }

    fun sendMessage(message: Message) {
        db.collection("ChatChannels").document(currentChannelId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    db.collection("Users").document(userId)
                            .collection("EngagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("lastMessage" to message), SetOptions.merge())

                    db.collection("Drivers").document(otherUserId)
                            .collection("EngagedChatChannels")
                            .document(userId)
                            .set(mapOf("lastMessage" to message), SetOptions.merge())
                }


    }


    private fun showRecycler() {

        messagesAdapter = activity?.let { MessagesAdapter(messages, it) }!!
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = messagesAdapter
        }


        binding.recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)

        addChatMessagesListener()
    }

    private fun showSampleRecycler() {

        val userName = context?.let { it1 -> SessionManager.with(it1).getUserName() }
        val samplesAdapter =  SamplesAdapter(messageSamples, this, userId,
            otherUserId, userName!!, context?.let { SessionManager.with(it).getLanguage() }!!)
        binding.recyclerViewSamples.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = samplesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}