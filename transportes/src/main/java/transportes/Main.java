package transportes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String DB_PATH = "transportes.odb"; // Ruta de la base de datos por defecto
    private static String currentDbPath = DB_PATH;
    private static Scanner scanner = new Scanner(System.in);
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Crear base de datos");
            System.out.println("2. Listar bases de datos creadas");
            System.out.println("3. Iniciar base de datos");
            System.out.println("4. Modificar o alterar una base de datos");
            System.out.println("5. Borrar una base de datos");
            System.out.println("6. Crear colecciones");
            System.out.println("7. Listar colecciones");
            System.out.println("8. Modificar colecciones");
            System.out.println("9. Eliminar colecciones");
            System.out.println("10. Crear campos");
            System.out.println("11. Listar campos");
            System.out.println("12. Modificar campos");
            System.out.println("13. Insertar campos");
            System.out.println("14. Borrar campos");
            System.out.println("15. Insertar datos en los atributos");
            System.out.println("16. Consultar datos");
            System.out.println("17. Modificar o actualizar datos");
            System.out.println("18. Borrar o eliminar datos");
            System.out.println("99. para depurar Valores Campo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (option) {
                case 1:
                    crearBaseDatos();
                    break;
                case 2:
                    listarBasesDatos();
                    break;
                case 3:
                    iniciarBaseDatos();
                    break;
                case 4:
                    modificarBaseDatos();
                    break;
                case 5:
                    borrarBaseDatos();
                    break;
                case 6:
                    crearColecciones();
                    break;
                case 7:
                    listarColecciones();
                    break;
                case 8:
                    modificarColecciones();
                    break;
                case 9:
                    eliminarColecciones();
                    break;
                case 10:
                    crearCampos();
                    break;
                case 11:
                    listarCampos();
                    break;
                case 12:
                    modificarCampos();
                    break;
                case 13:
                    insertarCampos();
                    break;
                case 14:
                    borrarCampos();
                    break;
                case 15:
                    insertarDatos();
                    break;
                case 16:
                    consultarDatos();
                    break;
                case 17:
                    modificarDatos();
                    break;
                case 18:
                    borrarDatos();
                    break;
                case 0:
                    exit = true;
                    cerrarConexion();
                    System.out.println("¡Hasta luego!");
                    break;
                case 99:
                    depurarValoresCampo();
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void crearBaseDatos() {
        System.out.print("Ingrese el nombre de la nueva base de datos: ");
        String nombreBD = scanner.nextLine();
        
        // Verificar si ya existe
        File file = new File(nombreBD + ".odb");
        if (file.exists()) {
            System.out.println("Error: La base de datos ya existe.");
            return;
        }
        
        // Crear la base de datos
        EntityManagerFactory tempEmf = null;
        EntityManager tempEm = null;
        
        try {
            tempEmf = Persistence.createEntityManagerFactory(nombreBD + ".odb");
            tempEm = tempEmf.createEntityManager();
            
            // Guardar información de la base de datos
            tempEm.getTransaction().begin();
            DatabaseInfo dbInfo = new DatabaseInfo(nombreBD);
            tempEm.persist(dbInfo);
            tempEm.getTransaction().commit();
            
            System.out.println("Base de datos '" + nombreBD + "' creada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al crear la base de datos: " + e.getMessage());
            if (tempEm != null && tempEm.getTransaction().isActive()) {
                tempEm.getTransaction().rollback();
            }
        } finally {
            if (tempEm != null) tempEm.close();
            if (tempEmf != null) tempEmf.close();
        }
    }

    private static void listarBasesDatos() {
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".odb"));
        
        if (files == null || files.length == 0) {
            System.out.println("No hay bases de datos creadas.");
            return;
        }
        
        System.out.println("\nBases de datos disponibles:");
        for (File file : files) {
            System.out.println("- " + file.getName());
        }
    }

    private static void iniciarBaseDatos() {
        // Cerrar conexión actual si existe
        cerrarConexion();
        
        // Listar bases de datos disponibles
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".odb"));
        
        if (files == null || files.length == 0) {
            System.out.println("No hay bases de datos disponibles para iniciar.");
            return;
        }
        
        System.out.println("\nBases de datos disponibles:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }
        
        System.out.print("Seleccione una base de datos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > files.length) {
            System.out.println("Selección no válida.");
            return;
        }
        
        currentDbPath = files[seleccion - 1].getName();
        
        try {
            emf = Persistence.createEntityManagerFactory(currentDbPath);
            em = emf.createEntityManager();
            System.out.println("Base de datos '" + currentDbPath + "' iniciada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al iniciar la base de datos: " + e.getMessage());
        }
    }

    private static void modificarBaseDatos() {
        // Listar bases de datos disponibles
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".odb"));
        
        if (files == null || files.length == 0) {
            System.out.println("No hay bases de datos disponibles para modificar.");
            return;
        }
        
        System.out.println("\nBases de datos disponibles:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }
        
        System.out.print("Seleccione una base de datos para modificar (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > files.length) {
            System.out.println("Selección no válida.");
            return;
        }
        
        String oldPath = files[seleccion - 1].getName();
        
        System.out.print("Ingrese el nuevo nombre para la base de datos: ");
        String newName = scanner.nextLine();
        
        if (!newName.endsWith(".odb")) {
            newName += ".odb";
        }
        
        File oldFile = new File(oldPath);
        File newFile = new File(newName);
        
        // Verificar si el nuevo nombre ya existe
        if (newFile.exists()) {
            System.out.println("Error: Ya existe una base de datos con ese nombre.");
            return;
        }
        
        // Cerrar conexión si es la base de datos actual
        if (oldPath.equals(currentDbPath)) {
            cerrarConexion();
        }
        
        // Renombrar el archivo
        if (oldFile.renameTo(newFile)) {
            System.out.println("Base de datos renombrada exitosamente.");
            
            // Actualizar la ruta actual si es necesario
            if (oldPath.equals(currentDbPath)) {
                currentDbPath = newName;
                
                // Reabrir la conexión
                try {
                    emf = Persistence.createEntityManagerFactory(currentDbPath);
                    em = emf.createEntityManager();
                } catch (Exception e) {
                    System.out.println("Error al reconectar a la base de datos: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Error al renombrar la base de datos.");
        }
    }

    private static void borrarBaseDatos() {
        // Listar bases de datos disponibles
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".odb"));
        
        if (files == null || files.length == 0) {
            System.out.println("No hay bases de datos disponibles para borrar.");
            return;
        }
        
        System.out.println("\nBases de datos disponibles:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }
        
        System.out.print("Seleccione una base de datos para borrar (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > files.length) {
            System.out.println("Selección no válida.");
            return;
        }
        
        String dbPath = files[seleccion - 1].getName();
        
        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar la base de datos '" + dbPath + "'? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Cerrar conexión si es la base de datos actual
        if (dbPath.equals(currentDbPath)) {
            cerrarConexion();
        }
        
        // Eliminar el archivo
        File file = new File(dbPath);
        if (file.delete()) {
            System.out.println("Base de datos eliminada exitosamente.");
            
            // Resetear la ruta actual si es necesario
            if (dbPath.equals(currentDbPath)) {
                currentDbPath = DB_PATH;
            }
        } else {
            System.out.println("Error al eliminar la base de datos.");
        }
    }

    private static void crearColecciones() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        System.out.print("Ingrese el nombre de la nueva colección: ");
        String nombreColeccion = scanner.nextLine();
        
        // Verificar si ya existe
        TypedQuery<Coleccion> query = em.createQuery(
            "SELECT c FROM Coleccion c WHERE c.nombre = :nombre", Coleccion.class);
        query.setParameter("nombre", nombreColeccion);
        
        List<Coleccion> colecciones = query.getResultList();
        if (!colecciones.isEmpty()) {
            System.out.println("Error: La colección ya existe.");
            return;
        }
        
        // Crear la colección
        try {
            em.getTransaction().begin();
            Coleccion coleccion = new Coleccion(nombreColeccion);
            em.persist(coleccion);
            em.getTransaction().commit();
            
            System.out.println("Colección '" + nombreColeccion + "' creada exitosamente.");
            
            // Preguntar si desea crear campos para esta colección
            System.out.print("¿Desea crear campos para esta colección ahora? (S/N): ");
            String respuesta = scanner.nextLine();
            
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.print("¿Cuántos campos desea crear? ");
                int numCampos = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
                
                if (numCampos <= 0) {
                    System.out.println("Número de campos no válido.");
                    return;
                }
                
                for (int i = 0; i < numCampos; i++) {
                    System.out.println("\nCreando campo " + (i + 1) + " de " + numCampos);
                    
                    System.out.print("Ingrese el nombre del campo: ");
                    String nombreCampo = scanner.nextLine();
                    
                    // Verificar si ya existe
                    TypedQuery<Campo> checkQuery = em.createQuery(
                        "SELECT c FROM Campo c WHERE c.nombre = :nombre AND c.coleccion = :coleccion", Campo.class);
                    checkQuery.setParameter("nombre", nombreCampo);
                    checkQuery.setParameter("coleccion", coleccion.getNombre());
                    
                    if (!checkQuery.getResultList().isEmpty()) {
                        System.out.println("Error: El campo ya existe en esta colección. Intente con otro nombre.");
                        i--; // Repetir esta iteración
                        continue;
                    }
                    
                    System.out.println("Tipos de datos disponibles:");
                    System.out.println("1. Texto");
                    System.out.println("2. Número");
                    System.out.println("3. Fecha");
                    System.out.println("4. Booleano");
                    System.out.print("Seleccione el tipo de dato (número): ");
                    int tipoSeleccion = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    
                    String tipoCampo;
                    switch (tipoSeleccion) {
                        case 1:
                            tipoCampo = "Texto";
                            break;
                        case 2:
                            tipoCampo = "Número";
                            break;
                        case 3:
                            tipoCampo = "Fecha";
                            break;
                        case 4:
                            tipoCampo = "Booleano";
                            break;
                        default:
                            System.out.println("Tipo no válido. Se usará Texto por defecto.");
                            tipoCampo = "Texto";
                    }
                    
                    // Crear el campo
                    try {
                        em.getTransaction().begin();
                        Campo campo = new Campo(nombreCampo, tipoCampo, coleccion.getNombre());
                        em.persist(campo);
                        em.getTransaction().commit();
                        
                        System.out.println("Campo '" + nombreCampo + "' creado exitosamente.");
                    } catch (Exception e) {
                        System.out.println("Error al crear el campo: " + e.getMessage());
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        i--; // Repetir esta iteración
                    }
                }
                
                System.out.println("\nTodos los campos han sido creados exitosamente.");
                
                // Preguntar si desea insertar datos
                System.out.print("¿Desea insertar datos en esta colección ahora? (S/N): ");
                respuesta = scanner.nextLine();
                
                if (respuesta.equalsIgnoreCase("S")) {
                    System.out.print("¿Cuántos registros desea insertar? ");
                    int numRegistros = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    
                    if (numRegistros <= 0) {
                        System.out.println("Número de registros no válido.");
                        return;
                    }
                    
                    // Obtener campos de la colección
                    TypedQuery<Campo> camposQuery = em.createQuery(
                        "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
                    camposQuery.setParameter("coleccion", coleccion.getNombre());
                    List<Campo> campos = camposQuery.getResultList();
                    
                    for (int i = 0; i < numRegistros; i++) {
                        System.out.println("\nInsertando registro " + (i + 1) + " de " + numRegistros);
                        
                        // Crear un nuevo dato
                        try {
                            em.getTransaction().begin();
                            
                            Dato dato = new Dato(coleccion.getNombre());
                            em.persist(dato);
                            
                            // Insertar valores para cada campo
                            for (Campo campo : campos) {
                                System.out.print("Ingrese valor para '" + campo.getNombre() + "' (" + campo.getTipo() + "): ");
                                String valor = scanner.nextLine();
                                
                                ValorCampo valorCampo = new ValorCampo(campo.getId(), valor);
                                valorCampo.setDatoId(dato.getId());
                                em.persist(valorCampo);
                            }
                            
                            em.getTransaction().commit();
                            
                            System.out.println("Registro " + (i + 1) + " insertado exitosamente.");
                        } catch (Exception e) {
                            System.out.println("Error al insertar datos: " + e.getMessage());
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            i--; // Repetir esta iteración
                        }
                    }
                    
                    System.out.println("\nTodos los registros han sido insertados exitosamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al crear la colección: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void listarColecciones() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones en la base de datos actual.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (Coleccion coleccion : colecciones) {
            System.out.println("- " + coleccion.getNombre());
        }
    }

    private static void modificarColecciones() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles para modificar.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para modificar (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        System.out.print("Ingrese el nuevo nombre para la colección: ");
        String nuevoNombre = scanner.nextLine();
        
        // Verificar si el nuevo nombre ya existe
        TypedQuery<Coleccion> checkQuery = em.createQuery(
            "SELECT c FROM Coleccion c WHERE c.nombre = :nombre", Coleccion.class);
        checkQuery.setParameter("nombre", nuevoNombre);
        
        if (!checkQuery.getResultList().isEmpty()) {
            System.out.println("Error: Ya existe una colección con ese nombre.");
            return;
        }
        
        // Modificar la colección
        try {
            em.getTransaction().begin();
            
            // Actualizar campos que hacen referencia a esta colección
            TypedQuery<Campo> camposQuery = em.createQuery(
                "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
            camposQuery.setParameter("coleccion", coleccion.getNombre());
            List<Campo> campos = camposQuery.getResultList();
            
            for (Campo campo : campos) {
                campo.setColeccion(nuevoNombre);
            }
            
            // Actualizar datos que hacen referencia a esta colección
            TypedQuery<Dato> datosQuery = em.createQuery(
                "SELECT d FROM Dato d WHERE d.coleccion = :coleccion", Dato.class);
            datosQuery.setParameter("coleccion", coleccion.getNombre());
            List<Dato> datos = datosQuery.getResultList();
            
            for (Dato dato : datos) {
                dato.setColeccion(nuevoNombre);
            }
            
            // Actualizar el nombre de la colección
            coleccion.setNombre(nuevoNombre);
            
            em.getTransaction().commit();
            
            System.out.println("Colección modificada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al modificar la colección: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void eliminarColecciones() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles para eliminar.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para eliminar (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar la colección '" + coleccion.getNombre() + "'? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Eliminar la colección y sus campos asociados
        try {
            em.getTransaction().begin();
            
            // Eliminar campos asociados
            TypedQuery<Campo> camposQuery = em.createQuery(
                "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
            camposQuery.setParameter("coleccion", coleccion.getNombre());
            List<Campo> campos = camposQuery.getResultList();
            
            for (Campo campo : campos) {
                // Eliminar valores de campo asociados
                TypedQuery<ValorCampo> valoresQuery = em.createQuery(
                    "SELECT v FROM ValorCampo v WHERE v.campoId = :campoId", ValorCampo.class);
                valoresQuery.setParameter("campoId", campo.getId());
                List<ValorCampo> valores = valoresQuery.getResultList();
                
                for (ValorCampo valor : valores) {
                    em.remove(valor);
                }
                
                em.remove(campo);
            }
            
            // Eliminar datos asociados
            TypedQuery<Dato> datosQuery = em.createQuery(
                "SELECT d FROM Dato d WHERE d.coleccion = :coleccion", Dato.class);
            datosQuery.setParameter("coleccion", coleccion.getNombre());
            List<Dato> datos = datosQuery.getResultList();
            
            for (Dato dato : datos) {
                em.remove(dato);
            }
            
            // Eliminar la colección
            em.remove(coleccion);
            
            em.getTransaction().commit();
            
            System.out.println("Colección eliminada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar la colección: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void crearCampos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles. Cree una colección primero.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para agregar campos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        System.out.print("¿Cuántos campos desea crear? ");
        int numCampos = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (numCampos <= 0) {
            System.out.println("Número de campos no válido.");
            return;
        }
        
        for (int i = 0; i < numCampos; i++) {
            System.out.println("\nCreando campo " + (i + 1) + " de " + numCampos);
            
            System.out.print("Ingrese el nombre del campo: ");
            String nombreCampo = scanner.nextLine();
            
            // Verificar si ya existe
            TypedQuery<Campo> checkQuery = em.createQuery(
                "SELECT c FROM Campo c WHERE c.nombre = :nombre AND c.coleccion = :coleccion", Campo.class);
            checkQuery.setParameter("nombre", nombreCampo);
            checkQuery.setParameter("coleccion", coleccion.getNombre());
            
            if (!checkQuery.getResultList().isEmpty()) {
                System.out.println("Error: El campo ya existe en esta colección. Intente con otro nombre.");
                i--; // Repetir esta iteración
                continue;
            }
            
            System.out.println("Tipos de datos disponibles:");
            System.out.println("1. Texto");
            System.out.println("2. Número");
            System.out.println("3. Fecha");
            System.out.println("4. Booleano");
            System.out.print("Seleccione el tipo de dato (número): ");
            int tipoSeleccion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea
            
            String tipoCampo;
            switch (tipoSeleccion) {
                case 1:
                    tipoCampo = "Texto";
                    break;
                case 2:
                    tipoCampo = "Número";
                    break;
                case 3:
                    tipoCampo = "Fecha";
                    break;
                case 4:
                    tipoCampo = "Booleano";
                    break;
                default:
                    System.out.println("Tipo no válido. Se usará Texto por defecto.");
                    tipoCampo = "Texto";
            }
            
            // Crear el campo
            try {
                em.getTransaction().begin();
                Campo campo = new Campo(nombreCampo, tipoCampo, coleccion.getNombre());
                em.persist(campo);
                em.getTransaction().commit();
                
                System.out.println("Campo '" + nombreCampo + "' creado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al crear el campo: " + e.getMessage());
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                i--; // Repetir esta iteración
            }
        }
        
        System.out.println("\nTodos los campos han sido creados exitosamente.");
    }

    private static void listarCampos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para ver sus campos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Listar campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        if (campos.isEmpty()) {
            System.out.println("No hay campos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        System.out.println("\nCampos de la colección '" + coleccion.getNombre() + "':");
        for (Campo campo : campos) {
            System.out.println("- " + campo.getNombre() + " (Tipo: " + campo.getTipo() + ")");
        }
    }

    private static void modificarCampos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Listar campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        if (campos.isEmpty()) {
            System.out.println("No hay campos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        System.out.println("\nCampos de la colección '" + coleccion.getNombre() + "':");
        for (int i = 0; i < campos.size(); i++) {
            System.out.println((i + 1) + ". " + campos.get(i).getNombre() + " (Tipo: " + campos.get(i).getTipo() + ")");
        }
        
        System.out.print("Seleccione un campo para modificar (número): ");
        int campoSeleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (campoSeleccion < 1 || campoSeleccion > campos.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Campo campo = campos.get(campoSeleccion - 1);
        
        System.out.print("Ingrese el nuevo nombre para el campo (deje en blanco para mantener el actual): ");
        String nuevoNombre = scanner.nextLine();
        
        if (!nuevoNombre.isEmpty()) {
            // Verificar si el nuevo nombre ya existe
            TypedQuery<Campo> checkQuery = em.createQuery(
                "SELECT c FROM Campo c WHERE c.nombre = :nombre AND c.coleccion = :coleccion AND c.id != :id", Campo.class);
            checkQuery.setParameter("nombre", nuevoNombre);
            checkQuery.setParameter("coleccion", coleccion.getNombre());
            checkQuery.setParameter("id", campo.getId());
            
            if (!checkQuery.getResultList().isEmpty()) {
                System.out.println("Error: Ya existe un campo con ese nombre en esta colección.");
                return;
            }
        }
        
        System.out.println("Tipos de datos disponibles:");
        System.out.println("1. Texto");
        System.out.println("2. Número");
        System.out.println("3. Fecha");
        System.out.println("4. Booleano");
        System.out.print("Seleccione el nuevo tipo de dato (número, deje en blanco para mantener el actual): ");
        String tipoSeleccion = scanner.nextLine();
        
        String nuevoTipo = campo.getTipo();
        if (!tipoSeleccion.isEmpty()) {
            int tipoNum = Integer.parseInt(tipoSeleccion);
            switch (tipoNum) {
                case 1:
                    nuevoTipo = "Texto";
                    break;
                case 2:
                    nuevoTipo = "Número";
                    break;
                case 3:
                    nuevoTipo = "Fecha";
                    break;
                case 4:
                    nuevoTipo = "Booleano";
                    break;
                default:
                    System.out.println("Tipo no válido. Se mantendrá el tipo actual.");
            }
        }
        
        // Modificar el campo
        try {
            em.getTransaction().begin();
            
            if (!nuevoNombre.isEmpty()) {
                campo.setNombre(nuevoNombre);
            }
            
            campo.setTipo(nuevoTipo);
            
            em.getTransaction().commit();
            
            System.out.println("Campo modificado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al modificar el campo: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void insertarCampos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        System.out.print("¿Cuántos campos desea insertar? ");
        int numCampos = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (numCampos <= 0) {
            System.out.println("Número de campos no válido.");
            return;
        }
        
        for (int i = 0; i < numCampos; i++) {
            System.out.println("\nInsertando campo " + (i + 1) + " de " + numCampos);
            
            System.out.print("Ingrese el nombre del nuevo campo: ");
            String nombreCampo = scanner.nextLine();
            
            // Verificar si ya existe
            TypedQuery<Campo> checkQuery = em.createQuery(
                "SELECT c FROM Campo c WHERE c.nombre = :nombre AND c.coleccion = :coleccion", Campo.class);
            checkQuery.setParameter("nombre", nombreCampo);
            checkQuery.setParameter("coleccion", coleccion.getNombre());
            
            if (!checkQuery.getResultList().isEmpty()) {
                System.out.println("Error: El campo ya existe en esta colección. Intente con otro nombre.");
                i--; // Repetir esta iteración
                continue;
            }
            
            System.out.println("Tipos de datos disponibles:");
            System.out.println("1. Texto");
            System.out.println("2. Número");
            System.out.println("3. Fecha");
            System.out.println("4. Booleano");
            System.out.print("Seleccione el tipo de dato (número): ");
            int tipoSeleccion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea
            
            String tipoCampo;
            switch (tipoSeleccion) {
                case 1:
                    tipoCampo = "Texto";
                    break;
                case 2:
                    tipoCampo = "Número";
                    break;
                case 3:
                    tipoCampo = "Fecha";
                    break;
                case 4:
                    tipoCampo = "Booleano";
                    break;
                default:
                    System.out.println("Tipo no válido. Se usará Texto por defecto.");
                    tipoCampo = "Texto";
            }
            
            // Crear el campo
            try {
                em.getTransaction().begin();
                Campo campo = new Campo(nombreCampo, tipoCampo, coleccion.getNombre());
                em.persist(campo);
                em.getTransaction().commit();
                
                System.out.println("Campo '" + nombreCampo + "' insertado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al insertar el campo: " + e.getMessage());
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                i--; // Repetir esta iteración
            }
        }
        
        System.out.println("\nTodos los campos han sido insertados exitosamente.");
    }

    private static void borrarCampos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Listar campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        if (campos.isEmpty()) {
            System.out.println("No hay campos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        System.out.println("\nCampos de la colección '" + coleccion.getNombre() + "':");
        for (int i = 0; i < campos.size(); i++) {
            System.out.println((i + 1) + ". " + campos.get(i).getNombre() + " (Tipo: " + campos.get(i).getTipo() + ")");
        }
        
        System.out.print("Seleccione un campo para borrar (número): ");
        int campoSeleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (campoSeleccion < 1 || campoSeleccion > campos.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Campo campo = campos.get(campoSeleccion - 1);
        
        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar el campo '" + campo.getNombre() + "'? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Eliminar el campo y sus valores asociados
        try {
            em.getTransaction().begin();
            
            // Eliminar valores de campo asociados
            TypedQuery<ValorCampo> valoresQuery = em.createQuery(
                "SELECT v FROM ValorCampo v WHERE v.campoId = :campoId", ValorCampo.class);
            valoresQuery.setParameter("campoId", campo.getId());
            List<ValorCampo> valores = valoresQuery.getResultList();
            
            for (ValorCampo valor : valores) {
                em.remove(valor);
            }
            
            // Eliminar el campo
            em.remove(campo);
            
            em.getTransaction().commit();
            
            System.out.println("Campo eliminado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar el campo: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void insertarDatos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para insertar datos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Listar campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        if (campos.isEmpty()) {
            System.out.println("No hay campos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        System.out.print("¿Cuántos registros desea insertar? ");
        int numRegistros = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (numRegistros <= 0) {
            System.out.println("Número de registros no válido.");
            return;
        }
        
        for (int i = 0; i < numRegistros; i++) {
            System.out.println("\nInsertando registro " + (i + 1) + " de " + numRegistros);
            
            // Crear un nuevo dato
            try {
                em.getTransaction().begin();
                
                Dato dato = new Dato(coleccion.getNombre());
                em.persist(dato);
                
                // Asegurarse de que el dato tenga un ID antes de continuar
                em.flush();
                
                // Insertar valores para cada campo
                for (Campo campo : campos) {
                    System.out.print("Ingrese valor para '" + campo.getNombre() + "' (" + campo.getTipo() + "): ");
                    String valor = scanner.nextLine();
                    
                    ValorCampo valorCampo = new ValorCampo(campo.getId(), valor);
                    valorCampo.setDatoId(dato.getId());
                    em.persist(valorCampo);
                }
                
                em.getTransaction().commit();
                
                System.out.println("Registro " + (i + 1) + " insertado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al insertar datos: " + e.getMessage());
                e.printStackTrace(); // Imprimir la traza completa para depuración
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                i--; // Repetir esta iteración
            }
        }
        
        System.out.println("\nTodos los registros han sido insertados exitosamente.");
    }
    private static void depurarValoresCampo() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        System.out.println("\nDepuración de ValorCampo:");
        
        TypedQuery<ValorCampo> query = em.createQuery("SELECT v FROM ValorCampo v", ValorCampo.class);
        List<ValorCampo> valores = query.getResultList();
        
        if (valores.isEmpty()) {
            System.out.println("No hay valores de campo almacenados.");
            return;
        }
        
        for (ValorCampo valor : valores) {
            System.out.println("ID: " + valor.getId() + 
                               ", CampoID: " + valor.getCampoId() + 
                               ", DatoID: " + valor.getDatoId() + 
                               ", Valor: " + valor.getValor());
        }
    }

    private static void consultarDatos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para consultar datos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Obtener datos de la colección
        TypedQuery<Dato> datosQuery = em.createQuery(
            "SELECT d FROM Dato d WHERE d.coleccion = :coleccion", Dato.class);
        datosQuery.setParameter("coleccion", coleccion.getNombre());
        List<Dato> datos = datosQuery.getResultList();
        
        if (datos.isEmpty()) {
            System.out.println("No hay datos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        // Obtener campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        // Mostrar datos
        System.out.println("\nDatos de la colección '" + coleccion.getNombre() + "':");
        
        for (Dato dato : datos) {
            System.out.println("\nRegistro ID: " + dato.getId());
            
            // Obtener todos los valores para este dato de una sola vez
            TypedQuery<ValorCampo> valoresQuery = em.createQuery(
                "SELECT v FROM ValorCampo v WHERE v.datoId = :datoId", ValorCampo.class);
            valoresQuery.setParameter("datoId", dato.getId());
            List<ValorCampo> todosValores = valoresQuery.getResultList();
            
            // Crear un mapa para acceder rápidamente a los valores por campoId
            Map<Long, String> valoresPorCampo = new HashMap<>();
            for (ValorCampo valor : todosValores) {
                valoresPorCampo.put(valor.getCampoId(), valor.getValor());
            }
            
            for (Campo campo : campos) {
                String valor = valoresPorCampo.get(campo.getId());
                if (valor != null) {
                    System.out.println("  " + campo.getNombre() + ": " + valor);
                } else {
                    System.out.println("  " + campo.getNombre() + ": [sin valor]");
                }
            }
        }
    }

    private static void modificarDatos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para modificar datos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Obtener datos de la colección
        TypedQuery<Dato> datosQuery = em.createQuery(
            "SELECT d FROM Dato d WHERE d.coleccion = :coleccion", Dato.class);
        datosQuery.setParameter("coleccion", coleccion.getNombre());
        List<Dato> datos = datosQuery.getResultList();
        
        if (datos.isEmpty()) {
            System.out.println("No hay datos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        // Mostrar datos para selección
        System.out.println("\nDatos de la colección '" + coleccion.getNombre() + "':");
        
        for (int i = 0; i < datos.size(); i++) {
            Dato dato = datos.get(i);
            System.out.println((i + 1) + ". Registro ID: " + dato.getId());
        }
        
        System.out.print("Seleccione un registro para modificar (número): ");
        int datoSeleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (datoSeleccion < 1 || datoSeleccion > datos.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Dato dato = datos.get(datoSeleccion - 1);
        
        // Obtener campos de la colección
        TypedQuery<Campo> camposQuery = em.createQuery(
            "SELECT c FROM Campo c WHERE c.coleccion = :coleccion", Campo.class);
        camposQuery.setParameter("coleccion", coleccion.getNombre());
        List<Campo> campos = camposQuery.getResultList();
        
        // Mostrar campos para selección
        System.out.println("\nCampos disponibles:");
        for (int i = 0; i < campos.size(); i++) {
            Campo campo = campos.get(i);
            
            // Obtener valor actual
            TypedQuery<ValorCampo> valorQuery = em.createQuery(
                "SELECT v FROM ValorCampo v WHERE v.datoId = :datoId AND v.campoId = :campoId", ValorCampo.class);
            valorQuery.setParameter("datoId", dato.getId());
            valorQuery.setParameter("campoId", campo.getId());
            
            List<ValorCampo> valores = valorQuery.getResultList();
            String valorActual = valores.isEmpty() ? "[sin valor]" : valores.get(0).getValor();
            
            System.out.println((i + 1) + ". " + campo.getNombre() + " (Valor actual: " + valorActual + ")");
        }
        
        System.out.print("Seleccione un campo para modificar (número): ");
        int campoSeleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (campoSeleccion < 1 || campoSeleccion > campos.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Campo campo = campos.get(campoSeleccion - 1);
        
        // Obtener valor actual
        TypedQuery<ValorCampo> valorQuery = em.createQuery(
            "SELECT v FROM ValorCampo v WHERE v.datoId = :datoId AND v.campoId = :campoId", ValorCampo.class);
        valorQuery.setParameter("datoId", dato.getId());
        valorQuery.setParameter("campoId", campo.getId());
        
        List<ValorCampo> valores = valorQuery.getResultList();
        
        System.out.print("Ingrese el nuevo valor para '" + campo.getNombre() + "': ");
        String nuevoValor = scanner.nextLine();
        
        try {
            em.getTransaction().begin();
            
            if (valores.isEmpty()) {
                // Crear nuevo valor
                ValorCampo valorCampo = new ValorCampo(campo.getId(), nuevoValor);
                valorCampo.setDatoId(dato.getId());
                em.persist(valorCampo);
            } else {
                // Actualizar valor existente
                ValorCampo valorCampo = valores.get(0);
                valorCampo.setValor(nuevoValor);
            }
            
            em.getTransaction().commit();
            
            System.out.println("Dato modificado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al modificar el dato: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void borrarDatos() {
        if (em == null) {
            System.out.println("Error: No hay una base de datos iniciada.");
            return;
        }
        
        // Listar colecciones disponibles
        TypedQuery<Coleccion> query = em.createQuery("SELECT c FROM Coleccion c", Coleccion.class);
        List<Coleccion> colecciones = query.getResultList();
        
        if (colecciones.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }
        
        System.out.println("\nColecciones disponibles:");
        for (int i = 0; i < colecciones.size(); i++) {
            System.out.println((i + 1) + ". " + colecciones.get(i).getNombre());
        }
        
        System.out.print("Seleccione una colección para borrar datos (número): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (seleccion < 1 || seleccion > colecciones.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Coleccion coleccion = colecciones.get(seleccion - 1);
        
        // Obtener datos de la colección
        TypedQuery<Dato> datosQuery = em.createQuery(
            "SELECT d FROM Dato d WHERE d.coleccion = :coleccion", Dato.class);
        datosQuery.setParameter("coleccion", coleccion.getNombre());
        List<Dato> datos = datosQuery.getResultList();
        
        if (datos.isEmpty()) {
            System.out.println("No hay datos en la colección '" + coleccion.getNombre() + "'.");
            return;
        }
        
        // Mostrar datos para selección
        System.out.println("\nDatos de la colección '" + coleccion.getNombre() + "':");
        
        for (int i = 0; i < datos.size(); i++) {
            Dato dato = datos.get(i);
            System.out.println((i + 1) + ". Registro ID: " + dato.getId());
        }
        
        System.out.print("Seleccione un registro para borrar (número): ");
        int datoSeleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        
        if (datoSeleccion < 1 || datoSeleccion > datos.size()) {
            System.out.println("Selección no válida.");
            return;
        }
        
        Dato dato = datos.get(datoSeleccion - 1);
        
        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar este registro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Eliminar el dato y sus valores asociados
        try {
            em.getTransaction().begin();
            
            // Eliminar valores de campo asociados
            TypedQuery<ValorCampo> valoresQuery = em.createQuery(
                "SELECT v FROM ValorCampo v WHERE v.datoId = :datoId", ValorCampo.class);
            valoresQuery.setParameter("datoId", dato.getId());
            List<ValorCampo> valores = valoresQuery.getResultList();
            
            for (ValorCampo valor : valores) {
                em.remove(valor);
            }
            
            // Eliminar el dato
            em.remove(dato);
            
            em.getTransaction().commit();
            
            System.out.println("Registro eliminado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar el registro: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private static void cerrarConexion() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

@Entity
class DatabaseInfo {
    @Id
    @GeneratedValue
    private long id;
    private String nombre;
    
    public DatabaseInfo() {}
    
    public DatabaseInfo(String nombre) {
        this.nombre = nombre;
    }
    
    public long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

@Entity
class Coleccion {
    @Id
    private String nombre;
    
    public Coleccion() {}
    
    public Coleccion(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

@Entity
class Campo {
    @Id
    @GeneratedValue
    private long id;
    private String nombre;
    private String tipo;
    private String coleccion;
    
    public Campo() {}
    
    public Campo(String nombre, String tipo, String coleccion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.coleccion = coleccion;
    }
    
    public long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getColeccion() {
        return coleccion;
    }
    
    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }
}

@Entity
class Dato {
    @Id
    @GeneratedValue
    private long id;
    private String coleccion;
    
    public Dato() {}
    
    public Dato(String coleccion) {
        this.coleccion = coleccion;
    }
    
    public long getId() {
        return id;
    }
    
    public String getColeccion() {
        return coleccion;
    }
    
    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }
}

@Entity
class ValorCampo {
    @Id
    @GeneratedValue
    private long id;
    private long campoId;
    private long datoId;
    private String valor;
    
    public ValorCampo() {}
    
    public ValorCampo(long campoId, String valor) {
        this.campoId = campoId;
        this.valor = valor;
    }
    
    public long getId() {
        return id;
    }
    
    public long getCampoId() {
        return campoId;
    }
    
    public void setCampoId(long campoId) {
        this.campoId = campoId;
    }
    
    public long getDatoId() {
        return datoId;
    }
    
    public void setDatoId(long datoId) {
        this.datoId = datoId;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
}