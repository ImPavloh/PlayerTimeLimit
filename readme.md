<div align="center">

# PlayerTimeLimit ⌛

![PTL-Logo](https://www.spigotmc.org/data/resource_icons/109/109315.jpg?1681676883)

<a href="https://github.com/ImPavloh/PlayerTimeLimit"><img src="https://img.shields.io/github/stars/ImPavloh/PlayerTimeLimit?style=social"></a>
<a href="https://twitter.com/ImPavloh" target="_blank"><img src="https://img.shields.io/twitter/follow/nestframework.svg?style=social&label=Follow"></a>

</div>

Este plugin permite establecer un tiempo de juego máximo para los jugadores y ofrece comandos para administradores con el fin de gestionar y verificar el tiempo restante de los jugadores.

## Características 📜

- Establece un límite de tiempo de juego para jugadores en el servidor.
- Muestra el tiempo restante a los jugadores usando un BossBar.
- Comandos para administradores:
  - `/tiempo resetear <jugador>`: Reinicia el tiempo de juego del jugador especificado.
  - `/tiempo ver <jugador>`: Muestra el tiempo restante del jugador especificado.
- Permite a los jugadores mostrar u ocultar la barra de tiempo restante con el comando `/tiempo`.
- Configuración personalizable a través del archivo `config.yml`.

## Instalación 📑

1. Descarga la última versión del plugin PlayerTimeLimit desde [GitHub](https://github.com/ImPavloh/PlayerTimeLimit/releases) o desde [SpigotMC](https://www.spigotmc.org/resources/playertimelimit.109315/).
2. Copia el archivo `.jar` en la carpeta `plugins` de tu servidor de Minecraft.
3. Reinicia el servidor para cargar el plugin y generar el archivo de configuración `config.yml`.

## Configuración 📒

El plugin genera un archivo de configuración llamado `config.yml` en la carpeta `plugins/PlayerTimeLimit`. Puedes modificar este archivo para personalizar el comportamiento del plugin.Aquí tienes una descripción de algunas de las opciones disponibles:

```yaml
timeLimit: 14400 # Tiempo límite en segundos (4 horas por defecto)
timeBarColor: YELLOW # Color de la barra de tiempo restante
timeBarStyle: SEGMENTED_12 # Estilo de la barra de tiempo restante
infinite: [] # Lista de nombres de jugadores que no tienen límite de tiempo (administradores, por ejemplo)
```

## Licencia 📃
>  Este proyecto está licenciado bajo la licencia MIT. Consulta el archivo [LICENSE](https://github.com/ImPavloh/PlayerTimeLimit/blob/master/LICENSE) para obtener más información.
