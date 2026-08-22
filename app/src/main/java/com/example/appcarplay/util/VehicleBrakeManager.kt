package com.example.appcarplay.util

import android.content.Context

class VehicleBrakeManager(context: Context) {

    private var car: Any? = null
    private var carPropertyManager: Any? = null
    private var isCarApiAvailable: Boolean = false

    init {
        initCarApiSafely(context)
    }

    private fun initCarApiSafely(context: Context) {
        try {
            // Verifica dinamicamente se a classe da API Automotive existe no dispositivo
            Class.forName("android.car.Car")

            // Inicialização isolada do Car Service
            val carInstance = android.car.Car.createCar(context)
            car = carInstance
            carPropertyManager = carInstance?.getCarManager(android.car.Car.PROPERTY_SERVICE)
            isCarApiAvailable = true
        } catch (t: Throwable) {
            // Captura NoClassDefFoundError, ClassNotFoundException e exceções do serviço do carro
            isCarApiAvailable = false
            car = null
            carPropertyManager = null
        }
    }

    /**
     * Retorna se a API de Automotive do Android está disponível no dispositivo atual
     */
    fun isAutomotiveSupported(): Boolean = isCarApiAvailable

    /**
     * Verifica se o freio de mão está puxado/ativo no sistema do carro.
     * Retorna false com segurança se a chave/propriedade não existir ou a API não for suportada.
     */
    fun isParkingBrakeOn(): Boolean {
        if (!isCarApiAvailable || carPropertyManager == null) {
            return false
        }

        return try {
            val manager = carPropertyManager as? android.car.hardware.property.CarPropertyManager
            manager?.getBooleanProperty(
                android.car.VehiclePropertyIds.PARKING_BRAKE_ON,
                0 // Area ID padrão (veículo todo)
            ) ?: false
        } catch (t: Throwable) {
            // Captura erros de propriedade inexistente (IllegalArgumentException), falta de permissão ou falha de IPC
            false
        }
    }
}