package com.smartphonesensing.corona.trustchain.peers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.tudelft.ipv8.Peer
import java.text.SimpleDateFormat
import java.util.*

class PeersViewModel : ViewModel() {

    private val _peerList = MutableLiveData<List<PeerListItem>>()
    val peerList: LiveData<List<PeerListItem>>
        get() = _peerList

    private val messageString: MutableLiveData<String> = MediatorLiveData()
    fun getMessageString(): MutableLiveData<String> {
        return messageString
    }
    fun setMessageString(string: String) {
        messageString.value = string
    }

    private val _selectedPeer = MutableLiveData<PeerListItem>()
    val selectedPeer: LiveData<PeerListItem>
        get() = _selectedPeer


    fun updatePeerList(peers: List<Peer>) {
        val peerItems = peers.map {
            PeerListItem(it)
        }
        _peerList.value = peerItems
    }

    fun onPeerClicked(peer: PeerListItem) {
        _selectedPeer.value = peer
    }



}

class PeerListItem(val peer: Peer) {
    val sdf = SimpleDateFormat("dd.MM HH:mm:ss")
    val text = peer.lastResponse?.let {"Last activity: ${sdf.format(peer.lastResponse!!)}"} ?: "No last activity"
    val toPeerString = "To peer:\n" + peer.mid
}

