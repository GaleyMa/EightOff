package juego.grafica;

import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

public class ColumnaView extends VBox {
    private static final double ANCHO = 110;
    private static final double ESPACIADO_CARTAS = -60;
    private static final double ALTO_MINIMO = 150;

    private int indice;
    private ObservableList<CartaView> cartas;
    private StackPane placeholder;

    public ColumnaView(int indice) {
        this.indice = indice;
        this.cartas = FXCollections.observableArrayList();

        setSpacing(ESPACIADO_CARTAS);
        setPadding(new Insets(5));
        setMinWidth(ANCHO);
        setPrefWidth(ANCHO);
        setMaxWidth(ANCHO);
        setMinHeight(ALTO_MINIMO);

        crearPlaceholder();
    }

    private void crearPlaceholder() {
        placeholder = new StackPane();
        placeholder.setPrefSize(CartaView.ANCHO, CartaView.ALTO);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(CartaView.ANCHO);
        imageView.setFitHeight(CartaView.ALTO);
        imageView.setPreserveRatio(true);
        imageView.setOpacity(0.6);
        try {
            Image reverso = new Image(getClass().getResourceAsStream("/imagenes/cartas/back.png"));
            imageView.setImage(reverso);
        } catch (Exception e) {
            Rectangle fondo = new Rectangle(CartaView.ANCHO, CartaView.ALTO);
            fondo.setFill(Color.rgb(70, 130, 180, 0.2));
            fondo.setStroke(Color.rgb(70, 130, 180, 0.4));
            fondo.setStrokeWidth(2);
            Double[] dashPattern = {5.0, 5.0};
            fondo.getStrokeDashArray().addAll(dashPattern);
            fondo.setArcWidth(10);
            fondo.setArcHeight(10);
            placeholder.getChildren().add(fondo);
            return;
        }

        placeholder.getChildren().add(imageView);
        getChildren().add(placeholder);
    }

    public void agregarCarta(CartaView cartaView) {
        if (cartaView == null) return;

        if (cartas.isEmpty() && getChildren().contains(placeholder)) {
            getChildren().remove(placeholder);
        }

        cartas.add(cartaView);
        getChildren().add(cartaView);
    }

    public CartaView quitarUltimaCarta() {
        if (cartas.isEmpty()) {
            return null;
        }

        CartaView carta = cartas.remove(cartas.size() - 1);
        getChildren().remove(carta);

        if (cartas.isEmpty() && !getChildren().contains(placeholder)) {
            getChildren().add(placeholder);
        }

        return carta;
    }

    public CartaView obtenerUltimaCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.get(cartas.size() - 1);
    }

    public void limpiar() {
        getChildren().clear();
        cartas.clear();
        getChildren().add(placeholder);
    }

    public boolean estaVacia() {
        return cartas.isEmpty();
    }

    public int getNumeroCartas() {
        return cartas.size();
    }

    /**
     * Verifica si una carta es parte de una secuencia movible
     */
    public boolean esParteDeSecuenciaMovible(CartaView carta) {
        int indiceCarta = cartas.indexOf(carta);
        if (indiceCarta == -1) return false;


        for (int i = indiceCarta; i < cartas.size() - 1; i++) {
            CartaView actual = cartas.get(i);
            CartaView siguiente = cartas.get(i + 1);

            if (!esConsecutivaMismoPalo(actual.getCarta(), siguiente.getCarta())) {
                return false;
            }
        }
        return true;
    }

    private boolean esConsecutivaMismoPalo(juego.logica.Carta carta1, juego.logica.Carta carta2) {
        return carta1.getPalo() == carta2.getPalo() &&
                carta1.getValor() == carta2.getValor() + 1;
    }

    public void resaltarSecuencia(CartaView cartaInicio) {
        int indiceInicio = cartas.indexOf(cartaInicio);
        if (indiceInicio == -1) return;

        for (int i = indiceInicio; i < cartas.size(); i++) {
            CartaView carta = cartas.get(i);
            carta.aplicarSeleccionSecuencia();
        }
    }

    public void quitarResaltadoSecuencia() {
        for (CartaView carta : cartas) {
            carta.quitarSeleccion();
        }
    }

    public int getIndice() {
        return indice;
    }

    public ObservableList<CartaView> getCartas() {
        return cartas;
    }

    @Override
    public String toString() {
        return "ColumnaView{indice=" + indice + ", cartas=" + cartas.size() + "}";
    }
}