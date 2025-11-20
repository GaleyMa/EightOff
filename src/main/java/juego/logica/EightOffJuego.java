package juego.logica;

public class EightOffJuego {

    public static class Config {
        public static final int NUM_CELDAS_LIBRES = 8;
        public static final int NUM_COLUMNAS = 8;
        public static final int NUM_FUNDACIONES = 4;
        public static final int TOTAL_CARTAS = 52;
    }

    private Carta[] celdasLibres;
    private ListaSimple<Carta>[] columnas;
    private ListaSimple<Carta>[] fundaciones;
    private ListaSimple<Movimiento> historial;
    private Baraja baraja;

    private boolean juegoGanado;
    private boolean juegoTerminado;
    private int numeroMovimientos;


    @SuppressWarnings("unchecked")
    public EightOffJuego() {
        celdasLibres = new Carta[Config.NUM_CELDAS_LIBRES];

        columnas = new ListaSimple[Config.NUM_COLUMNAS];
        for (int i = 0; i < Config.NUM_COLUMNAS; i++) {
            columnas[i] = new ListaSimple<>();
        }

        fundaciones = new ListaSimple[Config.NUM_FUNDACIONES];
        for (int i = 0; i < Config.NUM_FUNDACIONES; i++) {
            fundaciones[i] = new ListaSimple<>();
        }

        historial = new ListaSimple<>();

        juegoGanado = false;
        juegoTerminado = false;
        numeroMovimientos = 0;
    }


    public void iniciarJuego() {
        limpiarTablero();

        baraja = new Baraja();
        baraja.barajar();

        repartirCartas();

        juegoGanado = false;
        juegoTerminado = false;
        numeroMovimientos = 0;
    }


    private void limpiarTablero() {

        for (int i = 0; i < Config.NUM_CELDAS_LIBRES; i++) {
            celdasLibres[i] = null;
        }

        for (int i = 0; i < Config.NUM_COLUMNAS; i++) {
            columnas[i].limpiar();
        }

        for (int i = 0; i < Config.NUM_FUNDACIONES; i++) {
            fundaciones[i].limpiar();
        }

        // Limpiar historial
        historial.limpiar();
    }

    private void repartirCartas() {
        // Primero, repartir 4 cartas a las celdas libres
        for (int i = 0; i < 4; i++) {
            if (!baraja.estaVacia()) {
                Carta carta = baraja.repartir();
                carta.ponerBocaArriba();
                celdasLibres[i] = carta;
            }
        }

        // Luego repartir las 48 cartas restantes en 8 columnas (6 cada una)
        int cartasPorColumna = 6;

        for (int col = 0; col < Config.NUM_COLUMNAS; col++) {
            for (int i = 0; i < cartasPorColumna; i++) {
                if (!baraja.estaVacia()) {
                    Carta carta = baraja.repartir();
                    carta.ponerBocaArriba();
                    columnas[col].agregar(carta);
                }
            }
        }

        // Verificar que se repartieron todas las cartas
        int cartasEnCeldas = 0;
        for (int i = 0; i < Config.NUM_CELDAS_LIBRES; i++) {
            if (celdasLibres[i] != null) cartasEnCeldas++;
        }

        int cartasEnColumnas = 0;
        for (int i = 0; i < Config.NUM_COLUMNAS; i++) {
            cartasEnColumnas += columnas[i].getTamaño();
        }

        System.out.println("Repartición completada:");
        System.out.println("  Celdas libres: " + cartasEnCeldas + " cartas");
        System.out.println("  Columnas: " + cartasEnColumnas + " cartas");
        System.out.println("  Total: " + (cartasEnCeldas + cartasEnColumnas) + "/52 cartas");
    }

    /**
     * Intenta mover una carta de una zona a otra.
     */
    public boolean moverCarta(TipoZona origen, TipoZona destino,
                              int indiceOrigen, int indiceDestino) {
        if (juegoTerminado) {
            return false;
        }

        // Para Tableau a Tableau
        if (origen == TipoZona.TABLEAU && destino == TipoZona.TABLEAU) {
            return moverSecuenciaTableau(indiceOrigen, indiceDestino);
        }

        // Para movimientos simples (de otros orígenes)
        Carta carta = obtenerCartaDeZona(origen, indiceOrigen);
        if (carta == null) {
            return false;
        }

        if (!validarMovimiento(carta, origen, destino, indiceOrigen, indiceDestino)) {
            return false;
        }

        // Realizar el movimiento simple
        return realizarMovimientoSimple(origen, destino, indiceOrigen, indiceDestino, carta);
    }

    /**
     * Mueve una secuencia de cartas entre columnas del tableau
     */
    private boolean moverSecuenciaTableau(int indiceOrigen, int indiceDestino) {
        ListaSimple<Carta> columnaOrigen = columnas[indiceOrigen];
        ListaSimple<Carta> columnaDestino = columnas[indiceDestino];

        if (columnaOrigen.estaVacia()) {
            return false;
        }

        // Encontrar la secuencia completa que se puede mover
        ListaSimple<Carta> secuencia = obtenerSecuenciaMovible(columnaOrigen);
        if (secuencia.estaVacia()) {
            return false;
        }

        // Validar si la secuencia completa puede moverse al destino
        if (!validarMovimientoSecuenciaTableau(secuencia, indiceDestino)) {
            System.out.println("Movimiento Tableau->Tableau inválido para secuencia");
            return false;
        }

        // Mover toda la secuencia
        moverSecuenciaCompleta(secuencia, columnaOrigen, columnaDestino);

        // Guardar en historial (usamos la primera carta como referencia)
        Carta primeraCarta = secuencia.obtenerPrimero();
        Movimiento movimiento = new Movimiento(primeraCarta, TipoZona.TABLEAU, TipoZona.TABLEAU,
                indiceOrigen, indiceDestino);
        movimiento.setSecuenciaMovida(secuencia.getTamaño()); // Guardar cuántas cartas se movieron
        historial.agregar(movimiento);
        numeroMovimientos++;

        // Verificar estado del juego
        actualizarEstadoJuego();

        System.out.println("Movimiento Tableau->Tableau exitoso: " + secuencia.getTamaño() + " cartas");
        return true;
    }

    /**
     * Obtiene la secuencia completa de cartas que se pueden mover desde una columna
     */
    private ListaSimple<Carta> obtenerSecuenciaMovible(ListaSimple<Carta> columna) {
        ListaSimple<Carta> secuencia = new ListaSimple<>();

        if (columna.estaVacia()) {
            return secuencia;
        }

        // Siempre se puede mover al menos la última carta
        Carta ultimaCarta = columna.obtenerUltimo();
        secuencia.agregar(ultimaCarta);

        // Si hay más cartas, verificar si forman una secuencia válida
        int indiceActual = columna.getTamaño() - 2; // Empezar desde la penúltima carta

        while (indiceActual >= 0) {
            Carta cartaActual = columna.obtener(indiceActual);
            Carta cartaSiguiente = columna.obtener(indiceActual + 1);

            // Verificar si es consecutiva descendente del mismo palo
            if (cartaActual.getPalo() == cartaSiguiente.getPalo() &&
                    cartaActual.getValor() == cartaSiguiente.getValor() + 1) {
                secuencia.agregarAlInicio(cartaActual);
                indiceActual--;
            } else {
                break;
            }
        }

        System.out.println("Secuencia movible encontrada: " + secuencia.getTamaño() + " cartas");
        return secuencia;
    }

    /**
     * Valida si una secuencia completa puede moverse a otra columna
     */
    /**
     * Valida si una secuencia completa puede moverse a otra columna
     */
    private boolean validarMovimientoSecuenciaTableau(ListaSimple<Carta> secuencia, int indiceColumna) {
        ListaSimple<Carta> columnaDestino = columnas[indiceColumna];

        // Si la columna está vacía, solo puede ir un REY o una secuencia que empiece con REY
        if (columnaDestino.estaVacia()) {
            Carta primeraCartaSecuencia = secuencia.obtenerPrimero();
            boolean esRey = primeraCartaSecuencia.getRango() == Rango.REY;

            System.out.println("Validando movimiento a tableau vacío:");
            System.out.println("  Primera carta de secuencia: " + primeraCartaSecuencia + " (Rango: " + primeraCartaSecuencia.getRango() + ")");
            System.out.println("  Es Rey: " + esRey);
            System.out.println("  Resultado: " + esRey);

            return esRey;
        }

        // Si no está vacía, la primera carta de la secuencia debe ser consecutiva
        // descendente del mismo palo que la última carta del destino
        Carta primeraCartaSecuencia = secuencia.obtenerPrimero();
        Carta cartaSuperiorDestino = columnaDestino.obtenerUltimo();

        boolean mismoPalo = primeraCartaSecuencia.getPalo() == cartaSuperiorDestino.getPalo();
        boolean consecutivoDescendente = primeraCartaSecuencia.getValor() == cartaSuperiorDestino.getValor() - 1;

        System.out.println("Validando movimiento de secuencia Tableau:");
        System.out.println("  Primera carta de secuencia: " + primeraCartaSecuencia + " (Valor: " + primeraCartaSecuencia.getValor() + ")");
        System.out.println("  Carta superior destino: " + cartaSuperiorDestino + " (Valor: " + cartaSuperiorDestino.getValor() + ")");
        System.out.println("  Mismo palo: " + mismoPalo);
        System.out.println("  Consecutivo descendente: " + consecutivoDescendente);
        System.out.println("  Resultado: " + (mismoPalo && consecutivoDescendente));

        return mismoPalo && consecutivoDescendente;
    }

    /**
     * Mueve una secuencia completa de una columna a otra
     */
    private void moverSecuenciaCompleta(ListaSimple<Carta> secuencia,
                                        ListaSimple<Carta> columnaOrigen,
                                        ListaSimple<Carta> columnaDestino) {
        // Eliminar todas las cartas de la secuencia de la columna origen
        for (int i = 0; i < secuencia.getTamaño(); i++) {
            columnaOrigen.eliminarUltimo();
        }

        // Agregar todas las cartas a la columna destino
        for (int i = 0; i < secuencia.getTamaño(); i++) {
            Carta carta = secuencia.obtener(i);
            columnaDestino.agregar(carta);
        }
    }

    /**
     * Realiza un movimiento simple de una carta
     */
    private boolean realizarMovimientoSimple(TipoZona origen, TipoZona destino,
                                             int indiceOrigen, int indiceDestino, Carta carta) {
        // Realizar el movimiento
        quitarCartaDeZona(origen, indiceOrigen);
        colocarCartaEnZona(destino, indiceDestino, carta);

        // Guardar en historial
        Movimiento movimiento = new Movimiento(carta, origen, destino, indiceOrigen, indiceDestino);
        historial.agregar(movimiento);
        numeroMovimientos++;

        // Verificar estado del juego
        actualizarEstadoJuego();

        System.out.println("Movimiento exitoso: " + carta + " de " + origen + " a " + destino);
        return true;
    }

    /**
     * Valida si un movimiento es permitido según las reglas
     */
    private boolean validarMovimiento(Carta carta, TipoZona origen, TipoZona destino,
                                      int indiceOrigen, int indiceDestino) {
        if (!validarIndices(destino, indiceDestino)) {
            return false;
        }

        switch (destino) {
            case CELDA_LIBRE:
                return validarMovimientoACeldaLibre(indiceDestino);

            case TABLEAU:
                return validarMovimientoATableau(carta, indiceDestino);

            case FUNDACION:
                return validarMovimientoAFundacion(carta, indiceDestino);

            default:
                return false;
        }
    }

    /**
     * Valida índices según la zona
     */
    private boolean validarIndices(TipoZona zona, int indice) {
        switch (zona) {
            case CELDA_LIBRE:
                return indice >= 0 && indice < Config.NUM_CELDAS_LIBRES;
            case TABLEAU:
                return indice >= 0 && indice < Config.NUM_COLUMNAS;
            case FUNDACION:
                return indice >= 0 && indice < Config.NUM_FUNDACIONES;
            default:
                return false;
        }
    }

    /**
     * Valida si una carta puede moverse a una celda libre
     */
    private boolean validarMovimientoACeldaLibre(int indiceCelda) {
        return celdasLibres[indiceCelda] == null;
    }

    /**
     * Valida si una carta puede moverse a una columna del tableau
     */
    private boolean validarMovimientoATableau(Carta carta, int indiceColumna) {
        ListaSimple<Carta> columnaDestino = columnas[indiceColumna];

        // Si la columna está vacía, cualquier carta puede ir
        if (columnaDestino.estaVacia()) {
            System.out.println("  Tableau vacío - movimiento permitido");
            return true;
        }

        // Si no está vacía, debe ser consecutiva descendente del MISMO PALO
        Carta cartaSuperior = columnaDestino.obtenerUltimo();

        boolean mismoPalo = carta.getPalo() == cartaSuperior.getPalo();
        boolean consecutivoDescendente = carta.getValor() == cartaSuperior.getValor() - 1;

        System.out.println("Validando movimiento Tableau:");
        System.out.println("  Carta a mover: " + carta + " (Valor: " + carta.getValor() + ")");
        System.out.println("  Carta superior: " + cartaSuperior + " (Valor: " + cartaSuperior.getValor() + ")");
        System.out.println("  Mismo palo: " + mismoPalo);
        System.out.println("  Consecutivo descendente: " + consecutivoDescendente);
        System.out.println("  Resultado: " + (mismoPalo && consecutivoDescendente));

        return mismoPalo && consecutivoDescendente;
    }

    /**
     * Valida si una carta puede moverse a una fundación
     */
    private boolean validarMovimientoAFundacion(Carta carta, int indiceFundacion) {
        ListaSimple<Carta> fundacion = fundaciones[indiceFundacion];

        // Si está vacía, solo puede ser As
        if (fundacion.estaVacia()) {
            return carta.getRango() == Rango.AS;
        }

        // Si no está vacía, debe ser consecutiva ascendente del MISMO PALO
        // que la carta superior actual
        Carta cartaSuperior = fundacion.obtenerUltimo();
        return carta.getPalo() == cartaSuperior.getPalo() &&
                carta.getValor() == cartaSuperior.getValor() + 1;
    }

    /**
     * Obtiene una carta de una zona específica sin quitarla
     */
    private Carta obtenerCartaDeZona(TipoZona zona, int indice) {
        switch (zona) {
            case CELDA_LIBRE:
                System.out.println("Obteniendo carta de celda libre[" + indice + "]: " + celdasLibres[indice]);
                return celdasLibres[indice];

            case TABLEAU:
                if (columnas[indice].estaVacia()) {
                    System.out.println("Tableau[" + indice + "] está vacío");
                    return null;
                }
                Carta cartaTableau = columnas[indice].obtenerUltimo();
                System.out.println("Obteniendo carta de Tableau[" + indice + "]: " + cartaTableau);
                return cartaTableau;

            case FUNDACION:
                if (fundaciones[indice].estaVacia()) {
                    System.out.println("Fundación[" + indice + "] está vacía");
                    return null;
                }
                Carta cartaFundacion = fundaciones[indice].obtenerUltimo();
                System.out.println("Obteniendo carta de Fundación[" + indice + "]: " + cartaFundacion);
                return cartaFundacion;

            default:
                System.out.println("Zona desconocida: " + zona);
                return null;
        }
    }

    /**
     * Quita una carta de una zona específica
     */
    private void quitarCartaDeZona(TipoZona zona, int indice) {
        switch (zona) {
            case CELDA_LIBRE:
                celdasLibres[indice] = null;
                break;

            case TABLEAU:
                if (!columnas[indice].estaVacia()) {
                    columnas[indice].eliminarUltimo();
                }
                break;

            case FUNDACION:
                if (!fundaciones[indice].estaVacia()) {
                    fundaciones[indice].eliminarUltimo();
                }
                break;
        }
    }

    /**
     * Coloca una carta en una zona específica
     */
    private void colocarCartaEnZona(TipoZona zona, int indice, Carta carta) {
        switch (zona) {
            case CELDA_LIBRE:
                celdasLibres[indice] = carta;
                break;

            case TABLEAU:
                columnas[indice].agregar(carta);
                break;

            case FUNDACION:
                fundaciones[indice].agregar(carta);
                break;
        }
    }

    /**
     * Deshace el último movimiento realizado
     */
    public boolean deshacerMovimiento() {
        if (historial.estaVacia()) {
            return false;
        }

        Movimiento movimiento = historial.eliminarUltimo();

        // Si fue un movimiento de secuencia, deshacer múltiples cartas
        if (movimiento.getSecuenciaMovida() > 1 &&
                movimiento.getOrigen() == TipoZona.TABLEAU &&
                movimiento.getDestino() == TipoZona.TABLEAU) {

            // Deshacer múltiples cartas
            for (int i = 0; i < movimiento.getSecuenciaMovida(); i++) {
                Carta carta = columnas[movimiento.getIndiceDestino()].eliminarUltimo();
                columnas[movimiento.getIndiceOrigen()].agregar(carta);
            }
        } else {
            // Deshacer movimiento simple
            quitarCartaDeZona(movimiento.getDestino(), movimiento.getIndiceDestino());
            colocarCartaEnZona(movimiento.getOrigen(), movimiento.getIndiceOrigen(),
                    movimiento.getCarta());
        }

        numeroMovimientos--;

        // Actualizar estado
        actualizarEstadoJuego();

        return true;
    }

    /**
     * Obtiene una pista de movimiento válido.
     */
    public Movimiento obtenerPista() {
        ListaSimple<Movimiento> movimientos = buscarMovimientosDisponibles();

        if (movimientos.estaVacia()) {
            return null;
        }

        // Priorizar movimientos a fundación
        for (int i = 0; i < movimientos.getTamaño(); i++) {
            Movimiento mov = movimientos.obtener(i);
            if (mov.getDestino() == TipoZona.FUNDACION) {
                return mov;
            }
        }

        // Priorizar movimientos entre columnas
        for (int i = 0; i < movimientos.getTamaño(); i++) {
            Movimiento mov = movimientos.obtener(i);
            if (mov.getDestino() == TipoZona.TABLEAU) {
                return mov;
            }
        }

        // Retornar primer movimiento disponible
        return movimientos.obtenerPrimero();
    }

    /**
     * Busca todos los movimientos disponibles en el estado actual
     */
    private ListaSimple<Movimiento> buscarMovimientosDisponibles() {
        ListaSimple<Movimiento> movimientos = new ListaSimple<>();

        // Buscar movimientos desde columnas
        for (int i = 0; i < Config.NUM_COLUMNAS; i++) {
            if (columnas[i].estaVacia()) continue;

            Carta carta = columnas[i].obtenerUltimo();

            // A fundaciones
            for (int f = 0; f < Config.NUM_FUNDACIONES; f++) {
                if (validarMovimientoAFundacion(carta, f)) {
                    movimientos.agregar(new Movimiento(carta, TipoZona.TABLEAU,
                            TipoZona.FUNDACION, i, f));
                }
            }

            // A otras columnas
            for (int j = 0; j < Config.NUM_COLUMNAS; j++) {
                if (i != j && validarMovimientoATableau(carta, j)) {
                    movimientos.agregar(new Movimiento(carta, TipoZona.TABLEAU,
                            TipoZona.TABLEAU, i, j));
                }
            }

            // A celdas libres
            for (int c = 0; c < Config.NUM_CELDAS_LIBRES; c++) {
                if (celdasLibres[c] == null) {
                    movimientos.agregar(new Movimiento(carta, TipoZona.TABLEAU,
                            TipoZona.CELDA_LIBRE, i, c));
                    break;
                }
            }
        }

        // Buscar movimientos desde celdas libres
        for (int c = 0; c < Config.NUM_CELDAS_LIBRES; c++) {
            if (celdasLibres[c] == null) continue;

            Carta carta = celdasLibres[c];

            // A fundaciones
            for (int f = 0; f < Config.NUM_FUNDACIONES; f++) {
                if (validarMovimientoAFundacion(carta, f)) {
                    movimientos.agregar(new Movimiento(carta, TipoZona.CELDA_LIBRE,
                            TipoZona.FUNDACION, c, f));
                }
            }

            // A columnas
            for (int col = 0; col < Config.NUM_COLUMNAS; col++) {
                if (validarMovimientoATableau(carta, col)) {
                    movimientos.agregar(new Movimiento(carta, TipoZona.CELDA_LIBRE,
                            TipoZona.TABLEAU, c, col));
                }
            }
        }

        return movimientos;
    }


    public boolean verificarVictoria() {
        int cartasEnFundaciones = 0;
        for (int i = 0; i < Config.NUM_FUNDACIONES; i++) {
            cartasEnFundaciones += fundaciones[i].getTamaño();
        }
        return cartasEnFundaciones == Config.TOTAL_CARTAS;
    }


    public boolean verificarJuegoTerminado() {
        if (verificarVictoria()) {
            return false; // Si ganó, no está "terminado sin victoria"
        }

        ListaSimple<Movimiento> movimientos = buscarMovimientosDisponibles();
        return movimientos.estaVacia();
    }

    private void actualizarEstadoJuego() {
        if (verificarVictoria()) {
            juegoGanado = true;
            juegoTerminado = true;
        } else {
            juegoGanado = false;
            juegoTerminado = verificarJuegoTerminado();
        }
    }
    public Carta[] getCeldasLibres() {
        return celdasLibres;
    }
    public ListaSimple<Carta> getColumna(int indice) {
        if (indice < 0 || indice >= Config.NUM_COLUMNAS) {
            throw new IndexOutOfBoundsException("Índice de columna inválido: " + indice);
        }
        return columnas[indice];
    }
    public ListaSimple<Carta> getFundacion(int indice) {
        if (indice < 0 || indice >= Config.NUM_FUNDACIONES) {
            throw new IndexOutOfBoundsException("Índice de fundación inválido: " + indice);
        }
        return fundaciones[indice];
    }
    public boolean isJuegoGanado() {
        return juegoGanado;
    }
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    public int getNumeroMovimientos() {
        return numeroMovimientos;
    }
    public boolean puedeDeshacer() {
        return !historial.estaVacia();
    }
    public int getTamañoHistorial() {
        return historial.getTamaño();
    }
}