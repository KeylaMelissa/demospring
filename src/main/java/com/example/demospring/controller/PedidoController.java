package com.example.demospring.controller;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demospring.model.PedidoRequest;
import com.example.demospring.model.RespuestaDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @PostMapping("/procesar")
    public ResponseEntity<RespuestaDTO> procesarPedido(
            @RequestHeader Map<String, String> headers,
            @RequestBody PedidoRequest pedido) {

        // log.info("REQUEST headers={}", headers);
        // log.info("REQUEST body producto={} cantidad={}",
        //         pedido.getProducto(), pedido.getCantidad());

        try {

             String mensaje = "Recibimos tu pedido de " 
                     + pedido.getCantidad() + " " 
                     + pedido.getProducto();
            // String mensaje = null;
            // mensaje.length();
            String idGenerado = UUID.randomUUID().toString();

            RespuestaDTO respuesta = 
                    new RespuestaDTO(mensaje, idGenerado, true);

            // log.info("RESPONSE response=\"{}\" codigo=\"200\"", 
            //         mensaje);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {

            log.error("ERROR procesando pedido: {}", e.getMessage(), e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RespuestaDTO(
                            "Error interno",
                            null,
                            false));
        }
    }
}