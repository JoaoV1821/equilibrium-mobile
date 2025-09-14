package com.ufpr.equilibrium.feature_professional

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FormViewModel : ViewModel() {
    val nome = MutableLiveData<String>()
    val cpf = MutableLiveData<String>()
    val telefone = MutableLiveData<String>()
    val dataNasc = MutableLiveData<String>()
    val sexo = MutableLiveData<String>()

    val escolaridade = MutableLiveData<String>()
    val nivelSocio = MutableLiveData<String>()
    val peso = MutableLiveData<Int>()
    val altura = MutableLiveData<Float>()
    val historicoQueda = MutableLiveData<Boolean>()

    val cep = MutableLiveData<String>()
    val numero = MutableLiveData<Int>()
    val rua = MutableLiveData<String>()
    val complemento = MutableLiveData<String>()
    val bairro = MutableLiveData<String>()
    val cidade = MutableLiveData<String>()
    val estado = MutableLiveData<String>()
    val uf = MutableLiveData <String>()
}
