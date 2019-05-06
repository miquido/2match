package com.miquido.stringstranslator.config


sealed class ConfigValidationResult

object ConfigValid : ConfigValidationResult()

class ConfigError(val errorMessages: List<String>) : ConfigValidationResult()

