# MusicAppAndroid 🎶

**MusicAppAndroid** é um aplicativo de reprodução de música simples desenvolvido para a plataforma Android. Ele permite que os usuários explorem uma lista de músicas, reproduzam suas faixas favoritas e gerenciem suas músicas diretamente no aplicativo.

## Funcionalidades 📱

- **Reprodução de Músicas**: Os usuários podem explorar e tocar músicas disponíveis.
- **Fila de Reprodução**: As músicas são organizadas em filas, permitindo navegação entre faixas.
- **Favoritos**: Os usuários podem salvar e visualizar suas músicas favoritas.
- **Player de Música**: Interface de reprodução com controles de play/pause, próxima faixa e faixa anterior.

## Tecnologias Utilizadas 💻

- **Kotlin**: Linguagem principal para desenvolvimento Android.
- **Room**: Banco de dados local para armazenar usuários e músicas favoritas.
- **Glide**: Biblioteca usada para carregamento de imagens de capas de músicas.
- **MediaPlayer**: Utilizado para reprodução de áudio.
- **Coroutines**: Para operações assíncronas.

## Estrutura do Projeto 📂

- `activities/`: Contém as principais Activities do aplicativo, como telas de Login, Registro, Tela Principal e Player de Música.
- `adapters/`: Contém os adapters para preencher as listas de músicas e de favoritos.
- `databases/`: Contém as configurações do banco de dados usando Room (para usuários e músicas favoritas).
- `services/`: Contém o serviço de reprodução de música em segundo plano.
- `viewmodels/`: Gerencia os dados de UI relacionados à reprodução de música.
- `models/`: Contém as classes de dados para música e usuário.

## Instalação 🚀

1. Clone o repositório:
   ```bash
   git clone https://github.com/AnaCezanoski/MusicAppAndroid.git
