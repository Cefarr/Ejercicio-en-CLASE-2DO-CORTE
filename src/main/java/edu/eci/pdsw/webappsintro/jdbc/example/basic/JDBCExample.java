/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */

public class JDBCExample {
     
    public static void main(String args[]){
        try {
            List<String> Respuesta=new ArrayList<String>();   
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("---------------------");
            
            
            int suCodigoECI=20983250;
            System.out.println("-------------------Vamos a entrar---");
            //registrarNuevoProducto(con, suCodigoECI, "CESAR EDUARDO", 99999999);            
            //registrarNuevoProducto(con, 100, "Televisor", 199);   
            int codigoPedido=2;
            Respuesta=nombresProductosPedido(con, codigoPedido);
            
            for(int i=0 ; i<Respuesta.size();i++){
                String co=Respuesta.get(i);
                System.out.println("El String es"+co);

            }
            int respp=valorTotalPedido(con, codigoPedido);
            System.out.println("El valor es : "+respp);
            con.commit();
                        
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
       String createProductString=
               "insert into ORD_PRODUCTOS values(?,?,?)";
       PreparedStatement createProduct= con.prepareStatement(createProductString);
       try{
            createProduct.setInt(1, codigo);
            createProduct.setString(2, nombre);
            createProduct.setInt(3, precio);           
            int i=createProduct.executeUpdate();
       }catch(SQLException e){
            if (con != null) {
                try {
                    System.err.print("Transactionholalalal");
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch(SQLException excep) {
//                    JDBCTutorialUtilities.printSQLException(excep);
                }
            }

       }      
        con.commit();
        //con.close();
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
    }
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido)throws SQLException{
        List<String> np=new LinkedList<>();
       PreparedStatement readProduct,readProduct2;
       String getProductString=
        "select producto_fk from ORD_DETALLES_PEDIDO  where  ORD_DETALLES_PEDIDO.pedido_fk=?" ;          
       readProduct= con.prepareStatement(getProductString);
       readProduct.setInt(1, codigoPedido);
       try{
           ResultSet rs=readProduct.executeQuery();
           String pru;
           int prue;
           while(rs.next()){
                prue=rs.getInt(1);
                String getProductString2=
                       "select codigo, nombre from ORD_PRODUCTOS where codigo=? ";
                readProduct2= con.prepareStatement(getProductString2);
                readProduct2.setInt(1, prue);               
                ResultSet rs2=readProduct2.executeQuery();               
                while(rs2.next()){
                    pru=rs2.getString(2);
                    System.out.println(rs2.getInt(1)+""+rs2.getString(2));
                    np.add(pru);
                }
           }
        }catch(SQLException e){
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch(SQLException excep) {
//                    JDBCTutorialUtilities.printSQLException(excep);
                }
            }

       }      
        con.commit();        
        //con.close();
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla       
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido)throws SQLException{
        int respuesta=0;
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultado del ResultSet
        PreparedStatement priceProduct,priceProduct2;
        String getPriceString=
         "select producto_fk from ORD_DETALLES_PEDIDO  where  ORD_DETALLES_PEDIDO.pedido_fk=?" ;          
        priceProduct= con.prepareStatement(getPriceString);
        priceProduct.setInt(1, codigoPedido);
        try{
            ResultSet rs=priceProduct.executeQuery();
            int pru;
            while(rs.next()){
                pru=rs.getInt(1);
                String getProductString2=
                       "select codigo, nombre from ORD_PRODUCTOS where codigo=? ";
                priceProduct2= con.prepareStatement(getProductString2);
                priceProduct2.setInt(1, pru);               
                ResultSet rs2=priceProduct2.executeQuery();               
                int prue;
                while(rs2.next()){
                    prue=rs2.getInt(1);
                    respuesta+=prue;
                }
            }
         }catch(SQLException e){
             if (con != null) {
                 try {
                     System.err.print("Transaction is being rolled back");
                     con.rollback();
                 } catch(SQLException excep) {
 //                    JDBCTutorialUtilities.printSQLException(excep);
                 }
             }

        }      
        con.commit();   
        return respuesta;
    }
}