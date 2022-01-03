package model;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DB {
    private static DB singleton;
    private static final String DB_NAME = "autoshop.db";

    public static DB get() {
        return singleton;
    }

    public static void init() {
        singleton = new DB();
    }

    private Connection c;

    private DB() {
        try {
            File file = new File(DB_NAME);
            if (!file.exists()) {
                System.out.println("File " + DB_NAME + " does not exist! Creating new database...");
                file.createNewFile();
            }
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + file);
            try {
                System.out.println("Connected to AutoShop Database");
                System.out.println("customer: " + c.createStatement().executeQuery("select count(*) from customer").getInt(1));
                System.out.println("vehicle: " + c.createStatement().executeQuery("select count(*) from vehicle").getInt(1));
                System.out.println("item: " + c.createStatement().executeQuery("select count(*) from item").getInt(1));
//                System.out.println("invoice: " + c.createStatement().execute("select * from invoice"));
                System.out.println("work_order: " + c.createStatement().executeQuery("select count(*) from work_order").getInt(1));
//                System.out.println("work_order_customer: " + c.createStatement().executeQuery("select count(*) from work_order_customer").getInt(1));
//                System.out.println("work_order_vehicle: " + c.createStatement().executeQuery("select count(*) from work_order_vehicle").getInt(1));
                System.out.println("work_order_item: " + c.createStatement().executeQuery("select count(*) from work_order_item").getInt(1));
                System.out.println("work_order_labor: " + c.createStatement().executeQuery("select count(*) from work_order_labor").getInt(1));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Some of the tables do not exist, re-initializing tables...");
                initTables();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tables are only initialized if they do not exist.
     * @throws SQLException
     */
    private void initTables() throws SQLException {
        Statement stmt = c.createStatement();
        stmt.addBatch("create table if not exists customer (" +
                "customer_id integer primary key autoincrement, " +
                "first_name text, " +
                "last_name text, " +
                "phone text, " +
                "company text, " +
                "street text, " +
                "city text, " +
                "state text, " +
                "zip text);");
        stmt.addBatch("create table if not exists vehicle (" +
                "vin text primary key, " +
                "year int, make text, " +
                "model text, " +
                "license_plate text, " +
                "color text, " +
                "engine text, " +
                "transmission text, " +
                "mileage_in text, " +
                "mileage_out text);");
        stmt.addBatch("create table if not exists item (" +
                "item_id text primary key, " +
                "desc text, " +
                "retail_price real, " +
                "list_price real, " +
                "taxable boolean, " +
                "quantity integer);");
//        stmt.addBatch("create table if not exists invoice (" +
//                "invoice_id integer primary key autoincrement);");
        stmt.addBatch("create table if not exists work_order (" +
                "work_order_id integer primary key autoincrement, " +
                "date_created date, " +
                "date_completed date" +
                "customer_first_name text, " +
                "customer_last_name text, " +
                "customer_phone text, " +
                "customer_company text, " +
                "customer_street text, " +
                "customer_city text, " +
                "customer_state text, " +
                "customer_zip text," +
                "vehicle_vin text, " +
                "vehicle_year int, " +
                "vehicle_make text, " +
                "vehicle_model text, " +
                "vehicle_license_plate text, " +
                "vehicle_color text, " +
                "vehicle_engine text, " +
                "vehicle_transmission text, " +
                "vehicle_mileage_in text, " +
                "vehicle_mileage_out text);");
        /*
        stmt.addBatch("create table if not exists work_order_customer (" +
                "work_order_customer_id integer primary key autoincrement, " +
                "work_order_id integer, " +
                "customer_first_name text, " +
                "customer_last_name text, " +
                "customer_phone text, " +
                "customer_company text, " +
                "customer_street text, " +
                "customer_city text, " +
                "customer_state text, " +
                "customer_zip text," +
                "foreign key(work_order_id) references work_order(work_order_id));");
        stmt.addBatch("create table if not exists work_order_vehicle (" +
                "work_order_vehicle_id integer primary key autoincrement, " +
                "work_order_id integer, " +
                "vehicle_vin text, " +
                "vehicle_year int, " +
                "vehicle_make text, " +
                "vehicle_model text, " +
                "vehicle_license_plate text, " +
                "vehicle_color text, " +
                "vehicle_engine text, " +
                "vehicle_mileage_in text, " +
                "vehicle_mileage_out text, " +
                "foreign key(work_order_id) references work_order(work_order_id));");
         */
        stmt.addBatch("create table if not exists work_order_item (" +
                "work_order_item_id integer primary key autoincrement, " +
                "work_order_id integer, " +
                "item_id text, " +
                "item_desc text, " +
                "item_retail_price real, " +
                "item_list_price real, " +
                "item_quantity integer, " +
                "item_taxable boolean, " +
                "foreign key(work_order_id) references work_order(work_order_id));");
        stmt.addBatch("create table if not exists work_order_labor (" +
                "work_order_labor_id integer primary key autoincrement, " +
                "work_order_id integer, " +
                "labor_code text, " +
                "labor_desc text, " +
                "labor_billed_hrs real," +
                "labor_rate real," +
                "labor_taxable boolean, " +
                "foreign key(work_order_id) references work_order(work_order_id));");
        stmt.executeBatch();
    }

    public void addCustomer(Customer customer) {
        try {
            PreparedStatement prepStmt = c.prepareStatement(
                    "insert into customer " +
                            "(first_name, last_name, phone, company, street, city, state, zip)" +
                            "values (?, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, customer.getFirstName());
            prepStmt.setString(2, customer.getLastName());
            prepStmt.setString(3, customer.getPhone());
            prepStmt.setString(4, customer.getCompany());
            prepStmt.setString(5, customer.getAddress().getStreet());
            prepStmt.setString(6, customer.getAddress().getCity());
            prepStmt.setString(7, customer.getAddress().getState());
            prepStmt.setString(8, customer.getAddress().getZip());
            prepStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(Customer customer) {
        try {
            PreparedStatement prepStmt = c.prepareStatement("update customer set " +
                    "first_name = ?, last_name = ?, phone = ?, company = ?, " +
                    "street = ?, city = ?, state = ?, zip = ?" +
                    "where customer_id=" + customer.getId());
            prepStmt.setString(1, customer.getFirstName());
            prepStmt.setString(2, customer.getLastName());
            prepStmt.setString(3, customer.getPhone());
            prepStmt.setString(4, customer.getCompany());
            prepStmt.setString(5, customer.getAddress().getStreet());
            prepStmt.setString(6, customer.getAddress().getCity());
            prepStmt.setString(7, customer.getAddress().getState());
            prepStmt.setString(8, customer.getAddress().getZip());
            prepStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomerById(int id) {
        try {
            Statement stmt = c.createStatement();
            stmt.execute("delete from customer where customer_id=" + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new LinkedList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("select * from customer;");
            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phone = rs.getString("phone");
                String company = rs.getString("company");
                String street = rs.getString("street");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String zip = rs.getString("zip");
                Address address = new Address(street, city, state, zip);
                Customer cus = new Customer(id, firstName, lastName, phone, company, address);
                list.add(cus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Customer> getFilteredCustomers(String strFirstName, String strLastName, String strCompany) {
        List<Customer> list = new LinkedList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * from customer " +
                            "where first_name like \"" + strFirstName + "%\" " +
                            "and last_name like \"" + strLastName + "%\" " +
                            "and company like \"" + strCompany + "%\";");
            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phone = rs.getString("phone");
                String company = rs.getString("company");
                String street = rs.getString("street");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String zip = rs.getString("zip");
                Address address = new Address(street, city, state, zip);
                Customer cus = new Customer(id, firstName, lastName, phone, company, address);
                list.add(cus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addVehicle(Vehicle vehicle) {
        try {
            PreparedStatement prepStmt = c.prepareStatement(
                    "insert into vehicle " +
                            "(vin, year, make, model, license_plate, color, engine, mileage_in, mileage_out)," +
                            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            prepStmt.setString(1, vehicle.getVin());
            prepStmt.setInt(2, vehicle.getYear());
            prepStmt.setString(3, vehicle.getMake());
            prepStmt.setString(4, vehicle.getModel());
            prepStmt.setString(5, vehicle.getLicensePlate());
            prepStmt.setString(6, vehicle.getColor());
            prepStmt.setString(7, vehicle.getEngine());
            prepStmt.setString(8, vehicle.getMileageIn());
            prepStmt.setString(9, vehicle.getMileageOut());
            prepStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVehicle(Vehicle vehicle) {
        try {
             PreparedStatement prepStmt = c.prepareStatement("update vehicle set " +
                     "year = ?, model = ?, license_plate = ?, color = ?, engine = ?, " +
                     "transmission = ?, mileage_in = ?, mileage_out = ? " +
                     "where vin=\"" + vehicle.getVin() + "\"");
            prepStmt.setString(1, vehicle.getVin());
            prepStmt.setInt(2, vehicle.getYear());
            prepStmt.setString(3, vehicle.getModel());
            prepStmt.setString(4, vehicle.getLicensePlate());
            prepStmt.setString(5, vehicle.getColor());
            prepStmt.setString(6, vehicle.getEngine());
            prepStmt.setString(7, vehicle.getTransmission());
            prepStmt.setString(8, vehicle.getMileageIn());
            prepStmt.setString(9, vehicle.getMileageOut());

            prepStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVehicleByVin(String vin) {
        try {
            Statement stmt = c.createStatement();
            stmt.execute("delete from vehicle where vin=\"" + vin + "\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public List<Vehicle> getAllVehicles() {
//        return null;
//    }

    public void addWorkOrder(WorkOrder workOrder) {

    }

    public WorkOrder getWorkOrderById(int workOrderId) {
        WorkOrder workOrder = null;
        try {
            Statement stmt = c.createStatement();
            ResultSet rsWorkOrder = stmt.executeQuery(
                    "select * from work_order " +
                            "where work_order_id = " + workOrderId);

            if (rsWorkOrder.next()) {
                Date dateCreated = rsWorkOrder.getDate(2);
                Date dateCompleted = rsWorkOrder.getDate(3);
                String firstName = rsWorkOrder.getString(4);
                String lastName = rsWorkOrder.getString(5);
                String phone = rsWorkOrder.getString(6);
                String company = rsWorkOrder.getString(7);
                String street = rsWorkOrder.getString(8);
                String city = rsWorkOrder.getString(9);
                String state = rsWorkOrder.getString(10);
                String zip = rsWorkOrder.getString(11);
                Address address = new Address(street, city, state, zip);
                Customer customer = new Customer(firstName, lastName, phone, company, address);
                String vin = rsWorkOrder.getString(12);
                int year = rsWorkOrder.getInt(13);
                String make = rsWorkOrder.getString(14);
                String model = rsWorkOrder.getString(15);
                String licensePlate = rsWorkOrder.getString(16);
                String color = rsWorkOrder.getString(17);
                String engine = rsWorkOrder.getString(18);
                String transmission = rsWorkOrder.getString(19);
                String mileageIn = rsWorkOrder.getString(20);
                String mileageOut = rsWorkOrder.getString(21);
                Vehicle vehicle = new Vehicle(vin, year, make, model, licensePlate, color, engine, transmission, mileageIn, mileageOut);
                workOrder = new WorkOrder(customer, vehicle);
                workOrder.setId(workOrderId);

                ResultSet rsItem = stmt.executeQuery("select * from work_order_item " +
                        "where work_order_id = " + workOrderId);
                while (rsItem.next()) {
                    String id = rsItem.getString(3);
                    String desc = rsItem.getString(4);
                    double retailPrice = rsItem.getDouble(5);
                    double listPrice = rsItem.getDouble(6);
                    int quantity = rsItem.getInt(7);
                    boolean taxable = rsItem.getBoolean(8);
                    Item item = new Item(id, desc, retailPrice, listPrice, quantity, taxable);
                    workOrder.addItem(item);
                }

                ResultSet rsLabor = stmt.executeQuery("select * from work_order_labor " +
                        "where work_order_id = " + workOrderId);
                while (rsLabor.next()) {
                    String laborCode = rsLabor.getString(3);
                    String desc = rsLabor.getString(4);
                    double billedHrs = rsLabor.getDouble(5);
                    double rate = rsLabor.getDouble(6);
                    boolean taxable = rsLabor.getBoolean(7);
                    Labor labor = new Labor(laborCode, desc, billedHrs, rate, taxable);
                    workOrder.addLabor(labor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workOrder;
    }

    public List<WorkOrder> getAllWorkOrders() {
        List<WorkOrder> list = new LinkedList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet workOrderSet = stmt.executeQuery("select work_order_id from work_order");
            while (workOrderSet.next()) {
                int workOrderId = workOrderSet.getInt(1);
                WorkOrder workOrder = getWorkOrderById(workOrderId);
                list.add(workOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

//    public List<Item> getAllItems() {
//        return null;
//    }

    public List<Item> getItemsByInvoiceId(int invoiceId) {
        List<Item> list = new LinkedList<>();
        return list;
    }

    public List<Labor> getLaborsByInvoiceId(int invoiceId) {
        List<Labor> list = new LinkedList<>();
        return list;
    }
}
