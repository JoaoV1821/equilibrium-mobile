package com.ufpr.equilibrium.utils

private fun isValidCPF(cpf: String): Boolean {
    val cleanedCPF = cpf.replace("\\D".toRegex(), "") // Remove caracteres não numéricos

    if (cleanedCPF.length != 11 || cleanedCPF.all { it == cleanedCPF[0] }) return false

    fun calculateDigit(cpfSlice: String, weights: IntProgression): Int {
        val sum = cpfSlice.mapIndexed { index, c -> c.digitToInt() * weights.elementAt(index) }.sum()
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }

    val digit1 = calculateDigit(cleanedCPF.substring(0, 9), 10 downTo 2)
    val digit2 = calculateDigit(cleanedCPF.substring(0, 10), 11 downTo 2)

    println(cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2)

    return cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2
}