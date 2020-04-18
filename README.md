# Practica-SD

## Ejercicio 1
~~~
Red P2P de carga, búsqueda y descarga de archivos siguiendo las siguientes pautas:
Existen dos tipos de nodos, Maestros y Extremos. Los primeros, son servidores centralizados replicados
(al menos 2 nodos) que disponen del listado actualizado de los nodos extremos y se encargan de
gestionar la E/S de los peers. Los segundos cumplen dos funciones en el sistema: realizan consultas
(como clientes) y atienden solicitudes (como servidores).
Funcionamiento:
-- Cada extremo dispone de un parámetro definido en un archivo de inicialización con las direcciones IP de
los nodos Maestros. Al iniciarse se contacta con un maestro el cual funciona como punto de acceso al
sistema e informa cuáles son los archivos que dispone para compartir. Luego, está atento a trabajar en
dos modos (cliente y servidor)
-- Como cliente, deriva consultas al nodo maestro y una vez obtenida la respuesta, seleccionará el/los
recursos que desee descargar y se contactará con el par correspondiente para descargar el/los
archivo/s.
-- Como servidor, recibe la consulta, revisa si matchea la consulta con alguno de los recursos disponibles
y devuelve los resultados al nodo que solicitó resultados.
A partir de los conceptos vistos en la teoría, critique este modelo y presente mejoras en su propuesta.
~~~


<p align="center">
<img src="1.png" title="graf 1">
</p>


<p align="center">
<img src="2.png" title="graf 2">
</p>

~~~
Funcionamiento:
Todas las conexiones se realizan mediante Sockets.
Para Iniciar un Servidor Maestro, se debe ejecutar la Clase IniciarMaestro la cual esta ubicada
dentro del paquete “ejer1.maestro”. La clase IniciarMaestro leerá el archivo Json de
configuración “dataConexion.json” ubicado en el directorio raíz del proyecto el cual contiene las
direcciones Ip:Puerto disponibles para los Nodos Maestros, y creara un nuevo NodoMaestro al
cual envía las direcciones Ip:Puerto en forma de Lista. La clase NodoMaestro intentara
“levantar” un servidor socket en alguna de las direcciones que recibió. En este punto se cuenta
con un servidor Maestro y la red P2P ya puede funcionar, solo que no dispone de un servidor
Secundario. Para contar con un servidor Secundario se debe ejecutar la clase IniciarMaestro
nuevamente, y si se cuenta con otra dirección Ip:Puerto disponibles se levantara el
ServidorMaestro Secundario.
Replicación:
Los Nodos Maestros mantienen una lista con los Nodos Extremos y los recursos que Disponen,
al realizar una modificación (alta o baja de nodo Extremo) sobre esa lista se intenta enviar la
misma a otro de los maestros disponibles en la lista de Maestros (dataConexion.json) para que
registren los cambios.

~~~
<p align="center">
<img src="3.png" title="graf 3">
</p>

  
