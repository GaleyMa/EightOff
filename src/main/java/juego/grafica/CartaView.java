package juego.grafica;

import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import juego.logica.Carta;
import juego.logica.Palo;

public class CartaView extends StackPane {
    static final double ANCHO = 100;
    static final double ALTO = 140;

    private Carta carta;
    private BooleanProperty seleccionada;
    private ImageView imageView;
    private DropShadow efectoSeleccion;
    private DropShadow efectoSecuencia;

    public CartaView(Carta carta) {
        this.carta = carta;
        this.seleccionada = new SimpleBooleanProperty(false);

        // Configurar efectos
        this.efectoSeleccion = new DropShadow(10, Color.GOLD);
        this.efectoSecuencia = new DropShadow(10, Color.DODGERBLUE);

        // Configuración básica
        setPrefSize(ANCHO, ALTO);

        // Crear ImageView para la carta
        imageView = new ImageView();
        imageView.setFitWidth(ANCHO);
        imageView.setFitHeight(ALTO);
        imageView.setPreserveRatio(true);

        getChildren().add(imageView);
        actualizarVisualizacion();
        configurarEventos();
    }

    private void actualizarVisualizacion() {
        if (carta.isBocaArriba()) {
            cargarImagenFrente();
        } else {
            cargarImagenReverso();
        }
    }
    public void aplicarResaltadoHistorial() {
        // Efecto visual para resaltar cartas en modo historial
        setEffect(new DropShadow(15, Color.YELLOW));
        setScaleX(1.05);
        setScaleY(1.05);
    }

    public void quitarResaltadoHistorial() {
        setEffect(null);
        setScaleX(1.0);
        setScaleY(1.0);
    }

    private void cargarImagenFrente() {
        try {
            String nombreArchivo = obtenerSufijoPalo(carta.getPalo()) + carta.getRango().getSimbolo() + ".png";
            String ruta = "/imagenes/cartas/" + nombreArchivo;
            Image imagen = new Image(getClass().getResourceAsStream(ruta));
            imageView.setImage(imagen);
        } catch (Exception e) {
            System.err.println("Error cargando imagen de carta: " + e.getMessage());
        }
    }

    private void cargarImagenReverso() {
        try {
            Image reverso = new Image(getClass().getResourceAsStream("/imagenes/cartas/back.png"));
            imageView.setImage(reverso);
        } catch (Exception e) {
            System.err.println("Error cargando reverso: " + e.getMessage());
        }
    }

    private String obtenerSufijoPalo(Palo palo) {
        return switch (palo) {
            case CORAZONES -> "c";
            case DIAMANTES -> "d";
            case TREBOLES -> "t";
            case PICAS -> "p";
        };
    }


    private void configurarEventos() {
        setOnMouseEntered(e -> {
            if (carta.isBocaArriba() && !seleccionada.get()) {
                setScaleX(1.05);
                setScaleY(1.05);
            }
        });

        setOnMouseExited(e -> {
            if (!seleccionada.get()) {
                setScaleX(1.0);
                setScaleY(1.0);
            }
        });
    }

    public void aplicarSeleccion() {
        seleccionada.set(true);
        setScaleX(1.1);
        setScaleY(1.1);
        setEffect(efectoSeleccion);
    }

    public void aplicarSeleccionSecuencia() {
        seleccionada.set(true);
        setScaleX(1.05);
        setScaleY(1.05);
        setEffect(efectoSecuencia);
    }

    public void quitarSeleccion() {
        seleccionada.set(false);
        setScaleX(1.0);
        setScaleY(1.0);
        setEffect(null);
    }

    // Getters simples
    public Carta getCarta() { return carta; }
    public boolean isSeleccionada() { return seleccionada.get(); }
    public BooleanProperty seleccionadaProperty() { return seleccionada; }

}