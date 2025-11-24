package com.ufpr.equilibrium.domain.risk

enum class RiskLevel { LOW, MODERATE, HIGH, UNKNOWN }

object RiskClassifier {
    fun classify(testType: String?, timeSeconds: Int): RiskLevel {
        return when (testType) {
            "TUG" -> when {
                timeSeconds in 10..20 -> RiskLevel.LOW
                timeSeconds in 21..29 -> RiskLevel.MODERATE
                timeSeconds >= 30 -> RiskLevel.HIGH
                else -> RiskLevel.UNKNOWN
            }

            "FTSTS" -> when {
                timeSeconds in 1..11 -> RiskLevel.LOW
                timeSeconds in 12..15 -> RiskLevel.MODERATE
                timeSeconds >= 16 -> RiskLevel.HIGH
                else -> RiskLevel.UNKNOWN
            }
            
            else -> RiskLevel.UNKNOWN
        }
    }
}


