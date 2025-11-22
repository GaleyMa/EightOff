package juego.grafica;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;

import juego.logica.Palo;

public class FundacionView extends StackPane {
    private static final double ANCHO = 100;
    private static final double ALTO = 140;

    private CartaView cartaView;
    private StackPane contenidoVacio;

    public FundacionView(int indice) {
        setPrefSize(ANCHO, ALTO);
        setMinSize(ANCHO, ALTO);
        setMaxSize(ANCHO, ALTO);
        crearContenidoVacio();
    }

    private void crearContenidoVacio() {
        contenidoVacio = new StackPane();
        contenidoVacio.setPrefSize(ANCHO, ALTO);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(ANCHO);
        imageView.setFitHeight(ALTO);
        imageView.setPreserveRatio(true);
        imageView.setOpacity(0.6);

        try {
            Image reverso = new Image(getClass().getResourceAsStream("/imagenes/cartas/back.png"));
            imageView.setImage(reverso);
        } catch (Exception e) {
            Rectangle fondo = new Rectangle(ANCHO, ALTO);
            fondo.setFill(Color.rgb(100, 100, 100, 0.15));
            fondo.setStroke(Color.rgb(100, 100, 100, 0.4));
            fondo.setStrokeWidth(2);
            fondo.getStrokeDashArray().addAll(5d, 5d);
            fondo.setArcWidth(10);
            fondo.setArcHeight(10);
            contenidoVacio.getChildren().add(fondo);

            Text textoAs = new Text("A");
            textoAs.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            textoAs.setFill(Color.rgb(100, 100, 100, 0.3));
            contenidoVacio.getChildren().add(textoAs);
            StackPane.setAlignment(textoAs, Pos.CENTER);
            return;
        }

        contenidoVacio.getChildren().add(imageView);
        getChildren().add(contenidoVacio);
    }

    public void agregarCarta(CartaView carta) {
        if (cartaView != null) {
            getChildren().remove(cartaView);
        }

        this.cartaView = carta;
        getChildren().remove(contenidoVacio);
        getChildren().add(cartaView);
    }

    public CartaView quitarCarta() {
        if (cartaView == null) {
            return null;
        }

        CartaView cartaQuitada = cartaView;
        getChildren().remove(cartaView);
        this.cartaView = null;

        if (!getChildren().contains(contenidoVacio)) {
            getChildren().add(contenidoVacio);
        }

        return cartaQuitada;
    }

    public boolean estaVacia() {
        return cartaView == null;
    }

    public Palo getPaloActual() {
        return cartaView != null ? cartaView.getCarta().getPalo() : null;
    }

    public int getValorActual() {
        return cartaView != null ? cartaView.getCarta().getValor() : 0;
    }

    public CartaView getCartaView() {
        // Retorna la carta actual o null si está vacía
        for (javafx.scene.Node node : getChildren()) {
            if (node instanceof CartaView) {
                return (CartaView) node;
            }
        }
        return null;
    }



    public void resaltar() {
        if (contenidoVacio.isVisible()) {
            ImageView imageView = (ImageView) contenidoVacio.getChildren().get(0);
            imageView.setOpacity(0.6);
        }
    }

    public void quitarResaltado() {
        if (contenidoVacio.isVisible()) {
            ImageView imageView = (ImageView) contenidoVacio.getChildren().get(0);
            imageView.setOpacity(0.3);
        }
    }

    @Override
    public String toString() {
        return "FundacionView{vacia=" + estaVacia() + ", palo=" + getPaloActual() + ", valor=" + getValorActual() + "}";
    }
}