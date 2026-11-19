package ejemploproyectofinal;

import java.util.Scanner;

class TrainTicketSystem {

    static Scanner sc = new Scanner(System.in);

    static final String[] LUGARES = {"Medellin", "Santa Fe", "Cisneros", "Puerto Berrio"};

    // Matrices de datos (columnas según cada entidad)
    static String[][] usuarios = new String[100][5]; // Id, Nombre, Apellido, Origen, Destino
    static String[][] cajeros = new String[20][5];   // Id, Nombre, Apellido, Rol, Password
    static String[][] trenes = new String[10][3];    // Maquina, Modelo, Capacidad
    static String[][] tickets = new String[100][7];  // Id, IdUsuario, IdTren, Silla, Origen, Destino, Precio
    static String[][] ventas = new String[100][5];   // Id, IdTicket, Precio, Cantidad, Total

    static int totalUsuarios = 0;
    static int totalCajeros = 0;
    static int totalTrenes = 0;
    static int totalTickets = 0;
    static int totalVentas = 0;

    public static void main(String[] args) {
        cargarTrenes();
        menuInicial();
    }

    static void cargarTrenes() {
        trenes[0] = new String[]{"1", "Modelo A", "4"};
        trenes[1] = new String[]{"2", "Modelo B", "4"};
        trenes[2] = new String[]{"3", "Modelo C", "4"};
        totalTrenes = 3;
    }

    // ---------------- MENÚS ----------------

    static void menuInicial() {
        int op;
        do {
            System.out.println("\n=== FERROCARRILES DE ANTIOQUIA ===");
            System.out.println("1. Registrar cajero");
            System.out.println("2. Iniciar sesión");
            System.out.println("0. Salir");
            op = leerEntero("Opción: ");

            if (op == 1) registrarCajero();
            else if (op == 2 && login()) dashboard();
            else if (op == 0) System.out.println("Hasta luego.");
            else System.out.println("Opción inválida.");
        } while (op != 0);
    }

    static void dashboard() {
        int op;
        do {
            System.out.println("\n=== DASHBOARD CAJERO ===");
            System.out.println("1. Usuarios");
            System.out.println("2. Trenes");
            System.out.println("3. Tickets");
            System.out.println("4. Ventas");
            System.out.println("0. Cerrar sesión");
            op = leerEntero("Opción: ");

            if (op == 1) menuEntidad("Usuario");
            else if (op == 2) menuEntidad("Tren");
            else if (op == 3) menuEntidad("Ticket");
            else if (op == 4) menuEntidad("Venta");
            else if (op == 0) System.out.println("Sesión cerrada.");
            else System.out.println("Opción inválida.");
        } while (op != 0);
    }

    static void menuEntidad(String entidad) {
        int op;
        do {
            System.out.println("\n--- " + entidad.toUpperCase() + "S ---");
            System.out.println("1. Registrar");
            System.out.println("2. Actualizar");
            System.out.println("3. Consultar por id");
            System.out.println("4. Consultar todos");
            System.out.println("5. Eliminar");
            System.out.println("0. Volver");
            op = leerEntero("Opción: ");

            switch (op) {
                case 1 -> registrar(entidad);
                case 2 -> actualizar(entidad);
                case 3 -> consultarPorId(entidad);
                case 4 -> consultarTodos(entidad);
                case 5 -> eliminar(entidad);
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    // ---------------- LOGIN ----------------

    static void registrarCajero() {
        if (totalCajeros >= cajeros.length) return;
        String id = leerTexto("Id: ");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String rol = leerTexto("Rol: ");
        String pass = leerTexto("Contraseña: ");

        cajeros[totalCajeros++] = new String[]{id, nombre, apellido, rol, pass};
        System.out.println("Cajero registrado.");
    }

    static boolean login() {
        String id = leerTexto("Id cajero: ");
        String pass = leerTexto("Contraseña: ");
        int pos = buscar(cajeros, totalCajeros, id);
        if (pos != -1 && cajeros[pos][4].equals(pass)) {
            System.out.println("Bienvenido, " + cajeros[pos][1] + " " + cajeros[pos][2]);
            return true;
        }
        System.out.println("Credenciales incorrectas.");
        return false;
    }

    // ---------------- CRUD ----------------

    static void registrar(String entidad) {
        switch (entidad) {
            case "Usuario" -> {
                if (totalUsuarios >= usuarios.length) return;
                String id = leerTexto("Id: ");
                String nombre = leerTexto("Nombre: ");
                String apellido = leerTexto("Apellido: ");
                String origen = leerLugar("Origen");
                String destino = leerLugar("Destino");
                usuarios[totalUsuarios++] = new String[]{id, nombre, apellido, origen, destino};
            }
            case "Tren" -> {
                if (totalTrenes >= trenes.length) return;
                String id = leerTexto("Id/Máquina: ");
                String modelo = leerTexto("Modelo: ");
                String cap = String.valueOf(leerEntero("Capacidad: "));
                trenes[totalTrenes++] = new String[]{id, modelo, cap};
            }
            case "Ticket" -> {
                if (totalTickets >= tickets.length) return;
                String id = leerTexto("Id ticket: ");
                String idUsu = leerTexto("Id usuario: ");
                if (buscar(usuarios, totalUsuarios, idUsu) == -1) {
                    System.out.println("Usuario no existe.");
                    return;
                }
                String idTren = leerTexto("Id tren: ");
                int posTren = buscar(trenes, totalTrenes, idTren);
                if (posTren == -1) {
                    System.out.println("Tren no existe.");
                    return;
                }
                int cap = Integer.parseInt(trenes[posTren][2]);
                int silla = leerEntero("Silla (1-" + cap + "): ");
                if (silla < 1 || silla > cap || sillaOcupada(idTren, String.valueOf(silla))) {
                    System.out.println("Silla inválida u ocupada.");
                    return;
                }
                String origen = leerLugar("Origen");
                String destino = leerLugar("Destino");
                double precio = leerDecimal("Precio: ");

                tickets[totalTickets++] = new String[]{
                        id, idUsu, idTren, String.valueOf(silla), origen, destino, String.valueOf(precio)
                };
            }
            case "Venta" -> {
                if (totalVentas >= ventas.length) return;
                String id = leerTexto("Id venta: ");
                String idTicket = leerTexto("Id ticket: ");
                int posTicket = buscar(tickets, totalTickets, idTicket);
                if (posTicket == -1) {
                    System.out.println("Ticket no existe.");
                    return;
                }
                int cantidad = leerEntero("Cantidad: ");
                double precio = Double.parseDouble(tickets[posTicket][6]);
                double total = precio * cantidad;

                ventas[totalVentas++] = new String[]{
                        id, idTicket, String.valueOf(precio), String.valueOf(cantidad), String.valueOf(total)
                };
                System.out.println("Total de la venta: " + total);
            }
        }
        System.out.println(entidad + " registrado.");
    }

    static void consultarTodos(String entidad) {
        String[][] m = matriz(entidad);
        int n = total(entidad);
        if (n == 0) {
            System.out.println("Sin registros.");
            return;
        }
        for (int i = 0; i < n; i++) {
            imprimirFila(entidad, m[i]);
        }
    }

    static void consultarPorId(String entidad) {
        String id = leerTexto("Id a consultar: ");
        int pos = buscar(matriz(entidad), total(entidad), id);
        if (pos == -1) {
            System.out.println("No encontrado.");
            return;
        }
        imprimirFila(entidad, matriz(entidad)[pos]);
    }

    static void actualizar(String entidad) {
        String id = leerTexto("Id a actualizar: ");
        String[][] m = matriz(entidad);
        int pos = buscar(m, total(entidad), id);
        if (pos == -1) {
            System.out.println("No encontrado.");
            return;
        }
        String[] cols = encabezados(entidad);
        for (int i = 1; i < cols.length; i++) {
            if (entidad.equals("Venta") && (i == 2 || i == 4)) continue;
            m[pos][i] = leerTexto("Nuevo " + cols[i] + " (" + m[pos][i] + "): ");
        }
        if (entidad.equals("Venta")) {
            int posTicket = buscar(tickets, totalTickets, m[pos][1]);
            if (posTicket != -1) {
                double precio = Double.parseDouble(tickets[posTicket][6]);
                int cant = Integer.parseInt(m[pos][3]);
                m[pos][2] = String.valueOf(precio);
                m[pos][4] = String.valueOf(precio * cant);
            }
        }
        System.out.println("Actualizado.");
    }

    static void eliminar(String entidad) {
        String id = leerTexto("Id a eliminar: ");
        String[][] m = matriz(entidad);
        int n = total(entidad);
        int pos = buscar(m, n, id);
        if (pos == -1) {
            System.out.println("No encontrado.");
            return;
        }
        for (int i = pos; i < n - 1; i++) {
            m[i] = m[i + 1];
        }
        m[n - 1] = null;
        ajustarTotal(entidad, n - 1);
        System.out.println("Eliminado.");
    }

    // ---------------- AUXILIARES ----------------

    static int buscar(String[][] m, int n, String id) {
        for (int i = 0; i < n; i++) {
            if (m[i][0].equals(id)) return i;
        }
        return -1;
    }

    static boolean sillaOcupada(String idTren, String silla) {
        for (int i = 0; i < totalTickets; i++) {
            if (tickets[i][2].equals(idTren) && tickets[i][3].equals(silla)) return true;
        }
        return false;
    }

    static void imprimirFila(String entidad, String[] fila) {
        String[] cols = encabezados(entidad);
        for (int i = 0; i < cols.length; i++) {
            System.out.println(cols[i] + ": " + fila[i]);
        }
        System.out.println("----------------");
    }

    static String leerLugar(String campo) {
        while (true) {
            String l = leerTexto(campo + " (Medellin, Santa Fe, Cisneros, Puerto Berrio): ");
            for (String lugar : LUGARES) {
                if (lugar.equalsIgnoreCase(l)) return lugar;
            }
            System.out.println("Destino/origen no válido.");
        }
    }

    static String leerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    static int leerEntero(String msg) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(msg));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }

    static double leerDecimal(String msg) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(msg).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    static String[][] matriz(String e) {
        return switch (e) {
            case "Usuario" -> usuarios;
            case "Tren" -> trenes;
            case "Ticket" -> tickets;
            default -> ventas;
        };
    }

    static int total(String e) {
        return switch (e) {
            case "Usuario" -> totalUsuarios;
            case "Tren" -> totalTrenes;
            case "Ticket" -> totalTickets;
            default -> totalVentas;
        };
    }

    static void ajustarTotal(String e, int nuevoTotal) {
        switch (e) {
            case "Usuario" -> totalUsuarios = nuevoTotal;
            case "Tren" -> totalTrenes = nuevoTotal;
            case "Ticket" -> totalTickets = nuevoTotal;
            default -> totalVentas = nuevoTotal;
        }
    }

    static String[] encabezados(String e) {
        return switch (e) {
            case "Usuario" -> new String[]{"Id", "Nombre", "Apellido", "Origen", "Destino"};
            case "Tren" -> new String[]{"Maquina", "Modelo", "Capacidad"};
            case "Ticket" -> new String[]{"Id", "IdUsuario", "IdTren", "Silla", "Origen", "Destino", "Precio"};
            default -> new String[]{"Id", "IdTicket", "Precio", "Cantidad", "Total"};
        };
    }
}