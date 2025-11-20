package juego.grafica;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/**
 * Componente gráfico que representa una celda libre en el juego Eight Off.
 * Muestra el reverso de carta (back.png) atenuado cuando está vacía.
 */
public class CeldaLibreView extends StackPane {
    private static final double ANCHO = 100;
    private static final double ALTO = 140;

    private int indice;
    private CartaView cartaView;
    private ImageView placeholder;

    public CeldaLibreView(int indice) {
        this.indice = indice;

        placeholder = new ImageView();
        placeholder.setFitWidth(ANCHO);
        placeholder.setFitHeight(ALTO);
        placeholder.setPreserveRatio(true);
        placeholder.setOpacity(0.6);
        try {
            Image reverso = new Image(getClass().getResourceAsStream("/imagenes/cartas/back.png"));
            placeholder.setImage(reverso);
        } catch (Exception e) {
            crearReversoSencillo();
        }

        getChildren().add(placeholder);
        setPrefSize(ANCHO, ALTO);
        setMinSize(ANCHO, ALTO);
        setMaxSize(ANCHO, ALTO);
    }

    private void crearReversoSencillo() {
        getChildren().clear();
        Rectangle fondo = new Rectangle(ANCHO, ALTO);
        fondo.setFill(Color.rgb(70, 130, 180, 0.3));
        fondo.setStroke(Color.rgb(70, 130, 180, 0.6));
        fondo.setStrokeWidth(2);
        fondo.setArcWidth(10);
        fondo.setArcHeight(10);
        getChildren().add(fondo);
    }

    public void colocarCarta(CartaView carta) {
        if (cartaView != null) {
            getChildren().remove(cartaView);
        }
        this.cartaView = carta;

        placeholder.setVisible(false);
        getChildren().add(cartaView);
    }

    public CartaView quitarCarta() {
        if (cartaView == null) {
            return null;
        }

        CartaView cartaQuitada = cartaView;
        getChildren().remove(cartaView);
        this.cartaView = null;

        placeholder.setVisible(true);

        return cartaQuitada;
    }

    public boolean estaVacia() {
        return cartaView == null;
    }

    public void resaltar() {
        placeholder.setOpacity(0.8); // Menos atenuado cuando está resaltado
    }

    public void quitarResaltado() {
        placeholder.setOpacity(0.4); // Volver a atenuado normal
    }

    public int getIndice() {
        return indice;
    }

    public CartaView getCartaView() {
        return cartaView;
    }


    @Override
    public String toString() {
        return "CeldaLibreView{indice=" + indice + ", vacia=" + estaVacia() + "}";
    }
}