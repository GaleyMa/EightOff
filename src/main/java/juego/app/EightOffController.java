package juego.app;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import juego.logica.*;
import juego.grafica.*;

import java.util.Optional;

/**
 * Controlador principal simplificado de Eight Off.
 */
public class EightOffController {
    @FXML private VBox vboxFundaciones;
    @FXML private HBox hboxCeldasLibres;
    @FXML private HBox hboxTableau;
    @FXML private Button btnDeshacer;
    @FXML private Button btnPista;
    @FXML private Label labelMovimientos;
    private EightOffJuego juego;
    private CeldaLibreView[] celdasLibresViews;
    private ColumnaView[] columnasViews;
    private FundacionView[] fundacionesViews;
    private CartaView cartaSeleccionada;
    private TipoZona origenSeleccionado;
    private int indiceOrigen;

    @FXML
    public void initialize() {
        inicializarJuego();
        inicializarComponentesGraficos();
        configurarEventHandlers();
        actualizarTableroCompleto();
    }

    private void inicializarJuego() {
        juego = new EightOffJuego();
        juego.iniciarJuego();
    }

    private void inicializarComponentesGraficos() {
        // Celdas libres - en HBox horizontal (8 en 1 fila)
        celdasLibresViews = new CeldaLibreView[8];
        for (int i = 0; i < 8; i++) {
            celdasLibresViews[i] = new CeldaLibreView(i);
            hboxCeldasLibres.getChildren().add(celdasLibresViews[i]);
        }

        // Fundaciones - en VBox vertical (4 en 1 columna)
        fundacionesViews = new FundacionView[4];
        for (int i = 0; i < 4; i++) {
            fundacionesViews[i] = new FundacionView(i);
            vboxFundaciones.getChildren().add(fundacionesViews[i]);
        }

        // Columnas
        columnasViews = new ColumnaView[8];
        for (int i = 0; i < 8; i++) {
            columnasViews[i] = new ColumnaView(i);
            hboxTableau.getChildren().add(columnasViews[i]);
        }

        // Configurar eventos para columnas vacías
        for (int i = 0; i < columnasViews.length; i++) {
            final int indice = i;
            ColumnaView columna = columnasViews[i];

            // Agregar evento al placeholder si existe
            columna.getChildren().stream()
                    .filter(node -> node instanceof StackPane)
                    .findFirst()
                    .ifPresent(placeholder -> {
                        placeholder.setOnMouseClicked(e -> {
                            if (cartaSeleccionada != null) {
                                intentarMoverCarta(TipoZona.TABLEAU, indice);
                            }
                        });
                    });
        }
    }

    private void configurarEventHandlers() {
        for (int i = 0; i < celdasLibresViews.length; i++) {
            final int indice = i;
            celdasLibresViews[i].setOnMouseClicked(event ->
                    manejarClickCeldaLibre(indice)
            );
        }

        for (int i = 0; i < fundacionesViews.length; i++) {
            final int indice = i;
            fundacionesViews[i].setOnMouseClicked(event ->
                    manejarClickFundacion(indice)
            );
        }
    }

    @FXML
    private void onNuevoJuego(ActionEvent event) {
        if (juego.getNumeroMovimientos() > 0 && !juego.isJuegoTerminado()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Nuevo Juego");
            confirmacion.setHeaderText("¿Iniciar un nuevo juego?");
            confirmacion.setContentText("Se perderá el progreso actual.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }
        }

        reiniciarJuego();
    }

    @FXML
    private void onDeshacer(ActionEvent event) {
        if (!juego.puedeDeshacer()) {
            mostrarMensaje("Deshacer", "No hay movimientos que deshacer.");
            return;
        }

        boolean exito = juego.deshacerMovimiento();
        if (exito) {
            deseleccionarCarta();
            actualizarTableroCompleto();
        }
    }

    @FXML
    private void onPista(ActionEvent event) {
        Movimiento pista = juego.obtenerPista();
        if (pista == null) {
            mostrarMensaje("Pista", "No hay movimientos disponibles.");
            return;
        }
        animarPista(pista);
    }

    private void reiniciarJuego() {
        juego.iniciarJuego();
        deseleccionarCarta();
        actualizarTableroCompleto();
    }

    private void manejarClickCeldaLibre(int indice) {
        CeldaLibreView celda = celdasLibresViews[indice];

        if (cartaSeleccionada == null) {
            // Si no hay carta seleccionada y la celda tiene carta, seleccionarla
            if (!celda.estaVacia()) {
                CartaView carta = celda.getCartaView();
                seleccionarCarta(carta, TipoZona.CELDA_LIBRE, indice);
            }
        } else {
            // Si ya hay una carta seleccionada...

            // CASO 1: Si la carta seleccionada es de ESTA MISMA celda libre → deseleccionar
            if (origenSeleccionado == TipoZona.CELDA_LIBRE && indiceOrigen == indice) {
                deseleccionarCarta();
            }
            // CASO 2: Si la carta seleccionada es de OTRO lugar y esta celda está VACÍA → intentar mover aquí
            else if (celda.estaVacia()) {
                intentarMoverCarta(TipoZona.CELDA_LIBRE, indice);
            }
            // CASO 3: Si la carta seleccionada es de OTRO lugar y esta celda tiene carta → no hacer nada
            // (las celdas libres solo pueden tener una carta)
            else {
                System.out.println("Celda libre ya ocupada - no se puede mover aquí");
            }
        }
    }


    private void manejarClickColumna(ColumnaView columna, CartaView carta) {
        int indice = columna.getIndice();

        // Si la columna está vacía, manejar click en el placeholder
        if (columna.estaVacia()) {
            if (cartaSeleccionada != null) {
                intentarMoverCarta(TipoZona.TABLEAU, indice);
            }
            return;
        }

        // VERIFICACIÓN MODIFICADA: Permitir seleccionar cualquier carta que sea parte de una secuencia válida
        boolean esParteDeSecuencia = columna.esParteDeSecuenciaMovible(carta);

        if (!esParteDeSecuencia) {
            System.out.println("Solo se pueden seleccionar cartas que formen parte de una secuencia válida");
            return;
        }

        // Si ya hay una carta seleccionada Y es la misma carta (click en carta ya seleccionada)
        if (cartaSeleccionada != null && cartaSeleccionada == carta) {
            // Deseleccionar la carta
            deseleccionarCarta();
            columna.quitarResaltadoSecuencia();
            return;
        }

        // Si ya hay una carta seleccionada Y es diferente carta
        if (cartaSeleccionada != null && cartaSeleccionada != carta) {
            // Intentar mover a esta columna
            intentarMoverCarta(TipoZona.TABLEAU, indice);
            columna.quitarResaltadoSecuencia();
            return;
        }

        // Si no hay carta seleccionada, seleccionar esta carta
        if (cartaSeleccionada == null) {
            // Seleccionar la carta (y resaltar toda la secuencia)
            seleccionarCarta(carta, TipoZona.TABLEAU, indice);
            columna.resaltarSecuencia(carta);
        }
    }

    private void manejarClickFundacion(int indice) {
        if (cartaSeleccionada != null) {
            intentarMoverCarta(TipoZona.FUNDACION, indice);
        }
    }

    private void seleccionarCarta(CartaView carta, TipoZona zona, int indice) {
        if (cartaSeleccionada != null) {
            cartaSeleccionada.quitarSeleccion();
        }

        cartaSeleccionada = carta;
        origenSeleccionado = zona;
        indiceOrigen = indice;
        carta.aplicarSeleccion();
    }

    private void deseleccionarCarta() {
        if (cartaSeleccionada != null) {
            cartaSeleccionada.quitarSeleccion();
            cartaSeleccionada = null;
            origenSeleccionado = null;
            indiceOrigen = -1;
        }
    }

    private void intentarMoverCarta(TipoZona destino, int indiceDestino) {
        if (cartaSeleccionada == null) {
            System.out.println("ERROR: No hay carta seleccionada");
            return;
        }

        System.out.println("Intentando mover carta:");
        System.out.println("  Carta: " + cartaSeleccionada.getCarta());
        System.out.println("  Origen: " + origenSeleccionado + "[" + indiceOrigen + "]");
        System.out.println("  Destino: " + destino + "[" + indiceDestino + "]");

        // Validación especial para Tableau vacío
        if (destino == TipoZona.TABLEAU) {
            ColumnaView columnaDestino = columnasViews[indiceDestino];

            if (columnaDestino.estaVacia()) {
                Carta carta = cartaSeleccionada.getCarta();
                boolean esRey = carta.getRango() == Rango.REY;

                if (!esRey) {
                    System.out.println("Movimiento inválido: Solo los Reyes (K) pueden colocarse en tableau vacío");
                    mostrarMensaje("Movimiento inválido",
                            "Solo los Reyes (K) pueden colocarse en columnas vacías.\n" +
                                    "Carta seleccionada: " + carta.getRango().getSimbolo());
                    animarMovimientoInvalido(cartaSeleccionada);
                    return;
                }
            }
        }

        // Validación especial para fundaciones (mantener tu código existente)
        if (destino == TipoZona.FUNDACION) {
            Carta carta = cartaSeleccionada.getCarta();
            FundacionView fundacion = fundacionesViews[indiceDestino];

            // Si la fundación está vacía, solo permitir As
            if (fundacion.estaVacia() && carta.getRango() != Rango.AS) {
                System.out.println("Movimiento inválido: Solo los As pueden iniciar una fundación");
                mostrarMensaje("Movimiento inválido", "Solo los As pueden iniciar una fundación.");
                animarMovimientoInvalido(cartaSeleccionada);
                return;
            }

            // Si la fundación no está vacía, validar secuencia
            if (!fundacion.estaVacia()) {
                Palo paloFundacion = fundacion.getPaloActual();
                int valorEsperado = fundacion.getValorActual() + 1;

                if (carta.getPalo() != paloFundacion || carta.getValor() != valorEsperado) {
                    System.out.println("Movimiento inválido: No es consecutivo ascendente del mismo palo");
                    mostrarMensaje("Movimiento inválido",
                            "Debe ser del mismo palo y el siguiente valor en secuencia.");
                    animarMovimientoInvalido(cartaSeleccionada);
                    return;
                }
            }
        }

        boolean exito = juego.moverCarta(
                origenSeleccionado,
                destino,
                indiceOrigen,
                indiceDestino
        );

        if (exito) {
            System.out.println("Movimiento EXITOSO");
            deseleccionarCarta();
            // Quitar resaltado de secuencia si existe
            if (origenSeleccionado == TipoZona.TABLEAU) {
                columnasViews[indiceOrigen].quitarResaltadoSecuencia();
            }
            actualizarTableroCompleto();
            validarEstadoJuego();
        } else {
            System.out.println("Movimiento FALLIDO - El juego rechazó el movimiento");

            // Mostrar mensaje específico para tableau vacío
            if (destino == TipoZona.TABLEAU && columnasViews[indiceDestino].estaVacia()) {
                Carta carta = cartaSeleccionada.getCarta();
                if (carta.getRango() != Rango.REY) {
                    mostrarMensaje("Movimiento inválido",
                            "Solo los Reyes (K) pueden colocarse en columnas vacías.\n" +
                                    "Carta seleccionada: " + carta.getRango().getSimbolo());
                }
            }

            animarMovimientoInvalido(cartaSeleccionada);
        }
    }

    private void actualizarTableroCompleto() {
        actualizarCeldasLibres();
        actualizarColumnas();
        actualizarFundaciones();
        actualizarEstadoJuego();
    }

    private void actualizarCeldasLibres() {
        Carta[] celdasLogicas = juego.getCeldasLibres();

        for (int i = 0; i < celdasLibresViews.length; i++) {
            CeldaLibreView vista = celdasLibresViews[i];
            Carta carta = celdasLogicas[i];

            vista.quitarCarta();
            if (carta != null) {
                CartaView cartaView = new CartaView(carta);
                vista.colocarCarta(cartaView);

                // NO agregamos eventos de click aquí porque ya los maneja manejarClickCeldaLibre
                // a través del evento configurado en configurarEventHandlers
            }
        }
    }

    private void actualizarColumnas() {
        for (int i = 0; i < columnasViews.length; i++) {
            ColumnaView vista = columnasViews[i];
            ListaSimple<Carta> columna = juego.getColumna(i);

            vista.limpiar();
            for (int j = 0; j < columna.getTamaño(); j++) {
                Carta carta = columna.obtener(j);
                CartaView cartaView = new CartaView(carta);

                final ColumnaView col = vista;
                final CartaView cv = cartaView;
                cartaView.setOnMouseClicked(e -> {
                    // Si ya está seleccionada, deseleccionar
                    if (cartaSeleccionada == cv) {
                        deseleccionarCarta();
                        col.quitarResaltadoSecuencia();
                    } else {
                        manejarClickColumna(col, cv);
                    }
                });

                vista.agregarCarta(cartaView);
            }
        }
    }

    private void actualizarFundaciones() {
        for (int i = 0; i < fundacionesViews.length; i++) {
            FundacionView vista = fundacionesViews[i];
            ListaSimple<Carta> fundacion = juego.getFundacion(i);

            if (fundacion.estaVacia()) {
                if (!vista.estaVacia()) {
                    vista.quitarCarta();
                }
            } else {
                Carta carta = fundacion.obtenerUltimo();
                CartaView cartaView = new CartaView(carta);

                // Agregar evento de click para deseleccionar si está seleccionada
                cartaView.setOnMouseClicked(e -> {
                    if (cartaSeleccionada == cartaView) {
                        deseleccionarCarta();
                    }
                });

                if (!vista.estaVacia()) {
                    vista.quitarCarta();
                }
                vista.agregarCarta(cartaView);
            }
        }
    }


    private void actualizarEstadoJuego() {
        labelMovimientos.setText("Movimientos: " + juego.getNumeroMovimientos());
        btnDeshacer.setDisable(!juego.puedeDeshacer());
    }

    private void validarEstadoJuego() {
        if (juego.isJuegoGanado()) {
            mostrarDialogoVictoria();
        } else if (juego.isJuegoTerminado()) {
            mostrarDialogoDerrota();
        }
    }

    private void mostrarDialogoVictoria() {
        Alert victoria = new Alert(Alert.AlertType.INFORMATION);
        victoria.setTitle("¡Ganaste!");
        victoria.setContentText("Movimientos: " + juego.getNumeroMovimientos());

        ButtonType btnNuevo = new ButtonType("Nuevo Juego");
        victoria.getButtonTypes().setAll(btnNuevo, ButtonType.CLOSE);

        victoria.showAndWait().ifPresent(response -> {
            if (response == btnNuevo) {
                reiniciarJuego();
            }
        });
    }

    private void mostrarDialogoDerrota() {
        Alert derrota = new Alert(Alert.AlertType.WARNING);
        derrota.setTitle("Juego Terminado");
        derrota.setHeaderText("No hay más movimientos posibles");
        derrota.setContentText("Movimientos: " + juego.getNumeroMovimientos());

        ButtonType btnNuevo = new ButtonType("Nuevo Juego");
        derrota.getButtonTypes().setAll(btnNuevo, ButtonType.CLOSE);

        derrota.showAndWait().ifPresent(response -> {
            if (response == btnNuevo) {
                reiniciarJuego();
            }
        });
    }

    private void animarPista(Movimiento pista) {
        CartaView cartaView = encontrarCartaView(pista);
        if (cartaView != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(400), cartaView);
            fade.setFromValue(1.0);
            fade.setToValue(0.3);
            fade.setCycleCount(4);
            fade.setAutoReverse(true);
            fade.play();
        }
    }

    private void animarMovimientoInvalido(CartaView carta) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), carta);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    private CartaView encontrarCartaView(Movimiento pista) {
        switch (pista.getOrigen()) {
            case CELDA_LIBRE:
                return celdasLibresViews[pista.getIndiceOrigen()].getCartaView();
            case TABLEAU:
                return columnasViews[pista.getIndiceOrigen()].obtenerUltimaCarta();
            case FUNDACION:
                return fundacionesViews[pista.getIndiceOrigen()].getCartaView();
            default:
                return null;
        }
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}