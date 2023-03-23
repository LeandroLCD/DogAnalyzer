package com.leandrolcd.dogedexmvvm.dogslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandrolcd.dogedexmvvm.Dog
import kotlinx.coroutines.launch

class DogListViewModel : ViewModel() {

    private val _dogList = MutableLiveData<List<Dog>>()

    val dogList: LiveData<List<Dog>> = _dogList

    private val _status = MutableLiveData<UiStatus<List<Dog>>>()

    val status: LiveData<UiStatus<List<Dog>>> = _status

    private val repository = DogRepository()

    init {
        dowloadDogs()
    }

    private fun dowloadDogs() {
        viewModelScope.launch {
            _status.value = UiStatus.Loading()
            handleResponseStatus(repository.dowloadDogs())
        }
    }

    private fun handleResponseStatus(uiStatus: UiStatus<List<Dog>>) {
        if (uiStatus is UiStatus.Success) {
            _dogList.value = uiStatus.data!!
        }
        _status.value = uiStatus
    }
}