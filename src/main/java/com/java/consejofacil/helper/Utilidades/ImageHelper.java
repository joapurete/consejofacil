package com.java.consejofacil.helper.Utilidades;

import com.java.consejofacil.helper.Alertas.AlertHelper;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;

public class ImageHelper {

    // Logger para gestionar informacion
    private static final Logger log = LoggerFactory.getLogger(ImageHelper.class);

    // Método para convertir un Image en bytes

    public static byte[] convertirImagenABytes(Image imagen) {
        try {
            if (imagen != null) {
                InputStream inputStream = URI.create(imagen.getUrl()).toURL().openStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int longitud;
                while (((longitud = inputStream.read(buffer)) != -1)) {
                    outputStream.write(buffer, 0, longitud);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("No se pudo convertir la imagen: " + e.getMessage());
            AlertHelper.mostrarMensaje(true, "Error", "No se pudo convertir la imagen correctamente!");
        }
        return null;
    }


    // Método para convertir bytes en un objeto Image

    public static Image convertirBytesAImage(byte[] bytesImagen) {
        try {
            return new Image(new ByteArrayInputStream(bytesImagen));
        } catch (Exception e) {
            log.error("No se pudo cargar la imagen: " + e.getMessage());
            AlertHelper.mostrarMensaje(true, "Error", "No se pudo cargar la imagen correctamente!");
        }
        return null;
    }

    // Metodo para redondear una imagen

    public static void redondearImagen(ImageView imagen) {
        // Creamos un rectangulo con las dimensiones de la imagen para usarlo como clip y redondearla
        Rectangle clip = new Rectangle(
                imagen.getFitWidth(), imagen.getFitHeight()
        );

        // Redondeamos el rectangulo para darle la forma deseada
        clip.setArcWidth(imagen.getFitWidth());
        clip.setArcHeight(imagen.getFitHeight());

        // Aplicamos el rectangulo como clip para redondear la imagen
        imagen.setClip(clip);

        // Capturamos la imagen con el efecto de redondeo y le ponemos un color de fondo transparente
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageRedondeada = imagen.snapshot(parameters, null);

        // Sacamos el clip para aplicar un efecto de sombra
        imagen.setClip(null);

        // Creamos un efecto de sombra para simular un borde
        imagen.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.GRAY, 1, 0.5, 0, 0));

        // Establecemos la imagen redondeada
        imagen.setImage(imageRedondeada);
    }

    // Metodo para colocar una imagen por defecto

    public static void colocarImagenPorDefecto(ImageView imageView){
        // Colocamos la foto de perfil por defecto
        URL urlDefault = ImageHelper.class.getResource("/images/default.png");
        imageView.setImage(urlDefault != null ? new Image(urlDefault.toString()) : null);
    }
}
