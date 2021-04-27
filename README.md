# seadhna

__NOTE:__ Archived. See [mps-youtube](https://github.com/mps-youtube/mps-youtube).

Mark YouTube channel video URLs to output to [stdout](https://en.wikipedia.org/wiki/Standard_streams#Standard_output_(stdout)). Uses channel RSS feeds to avoid using official YouTube API.

## Table Of Contents
- [seadhna](#seadhna)
  - [Table Of Contents](#table-of-contents)
  - [🏗️ Building](#️-building)
    - [Prerequisites](#prerequisites)
    - [Build](#build)
  - [🚀 Running](#-running)
    - [Option 1: java -jar command](#option-1-java--jar-command)
    - [Option 2: aliasing](#option-2-aliasing)

## 🏗️ Building

### Prerequisites

- [Java](https://openjdk.java.net/install/)
- [Maven](https://maven.apache.org/download.cgi)

### Build

```bash
> git clone https://github.com/pskenny/seadhna # Clone seadhna
> cd seadhna
> mvn clean compile assembly:single # Generate jar
> java -jar ./target/seadhna-0.1.0-jar-with-dependencies.jar # Run jar
```

## 🚀 Running

### Option 1: java -jar command

```bash
java -jar /path/to/jar/seadhna.jar
```

### Option 2: aliasing

Bash:

```bash
echo "alias seadhna=\"java -jar /path/to/jar/seadhna.jar\"" >> ~/.bashrc
```

Fish:

```bash
echo "alias seadhna=\"java -jar /path/to/jar/seadhna.jar\"" >> ~/.config/fish/config.fish
```
