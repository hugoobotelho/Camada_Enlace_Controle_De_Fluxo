/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 20/05/2023
* Ultima alteracao.: 31/05/2023
* Nome.............: Camada de Enlace de dados Controle de erro
* Funcao...........: Simular a camada enlace de dados de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;

import java.util.concurrent.Semaphore;

public class Transmissor {
  private int qtdCaracters;
  private int tipoDeCodificacao;
  private int tipoDeEnquadramento;
  private int qtdBitsTotais = 0;
  private int tipoControleErro;
  private int porcentagemErro;
  private int qtdBitsErrados;
  private int tipoControleFluxo;
  private String[] next_frame_to_send = { "00", "01", "10", "11" };
  private int indiceNextFrame = -1;
  private String ackJanela1Bit = "22";
  //private int indiceAck = -1;
  // MeioDeComunicao meioDeComunicao = new MeioDeComunicao();

  public void tipoDeCodificacao(int n) {
    this.tipoDeCodificacao = n;
  };

  public void tipoDeEnquadramento(int n) {
    this.tipoDeEnquadramento = n;
  };

  public void tipoControleErro(int n) {
    this.tipoControleErro = n;
  };

  public void porcentagemErro(int n) {
    this.porcentagemErro = n;
  };

  public void qtdBitsErrados(int n) {
    this.qtdBitsErrados = n;
  };

  public void tipoControleFluxo(int n) {
    this.tipoControleFluxo = n;
  };

  private void nextFrame() {
    indiceNextFrame++;
    if (indiceNextFrame > 3) {
      indiceNextFrame = 0;
    }
  }

  // private void nextAck() {
  //   indiceAck++;
  //   if (indiceAck > 3) {
  //     indiceAck = 0;
  //   }
  // }

  /*
   * ***************************************************************
   * Metodo: AplicacaoTransmissora.
   * Funcao: fazer aparacer o campo para digitar a mensagem e chamar a
   * CamadaDeAplicacaoTransmissora.
   * Parametros: sem parametros.
   * Retorno: sem retorno.
   */
  public void AplicacaoTransmissora() {
    TextArea textArea = new TextArea();
    Principal.root.getChildren().add(textArea);
    textArea.setLayoutX(63);
    textArea.setLayoutY(252);
    textArea.setStyle(
        "-fx-font-size: 14px;" + // Tamanho da fonte
            "-fx-border-radius: 10px;" + // Bordas arredondadas
            "-fx-padding: 10px;" + // Padding
            "-fx-background-color: #FFEEE5;" + // Cor de fundo
            "-fx-focus-color: transparent;" + // Cor de foco
            "-fx-faint-focus-color: transparent;" // Cor de foco fraco
    );

    // Definindo o tamanho do TextArea
    textArea.setPrefSize(110, 55);
    Button enviarButton = new Button("Enviar");
    enviarButton.setStyle(
        "-fx-font-size: 16px; " + // Tamanho da fonte
            "-fx-background-color: white; " + // Cor de fundo
            "-fx-text-fill: #435D7A; " + // Cor do texto
            "-fx-padding: 10px; " + // Padding
            "-fx-background-radius: 10px; " + // Bordas arredondadas
            "-fx-border-radius: 10px; " + // Bordas arredondadas
            "-fx-border-color: transparent;" // Cor da borda
    );
    // Mudando a cor do texto quando o mouse passa por cima
    enviarButton.setOnMouseEntered(e -> enviarButton.setStyle(
        "-fx-font-size: 16px; -fx-background-color: #435D7A; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 10px; fx-border-radius: 10px; -fx-border-color: transparent;"));
    // Voltando à cor original do texto quando o mouse sai de cima
    enviarButton.setOnMouseExited(e -> enviarButton.setStyle(
        "-fx-font-size: 16px; -fx-background-color: white; -fx-text-fill: #435D7A; -fx-padding: 10px; -fx-background-radius: 10px; fx-border-radius: 10px; -fx-border-color: transparent;"));

    enviarButton.setLayoutX(90);
    enviarButton.setLayoutY(400);

    enviarButton.setOnAction(e -> {
      qtdBitsTotais = 0;
      indiceNextFrame = -1;
      ackJanela1Bit = "22";
      String mensagem = textArea.getText();
      Principal.receptor.zeraBuffer();
      CamadaDeAplicacaoTransmissora(mensagem);
    });

    Principal.root.getChildren().add(enviarButton);

  }

  /*
   * ***************************************************************
   * Metodo: CamadaDeAplicacaoTransmissora.
   * Funcao: metodo para inserir os bits de cada caracter da mensagem em um array
   * de inteiros e chama a CamadaFisicaTransmissora.
   * Parametros: recebe uma mensagem do tipo String.
   * Retorno: sem retorno.
   */

  public void CamadaDeAplicacaoTransmissora(String mensagem) {
    char[] arrayDeCaracteres = mensagem.toCharArray();
    qtdCaracters = arrayDeCaracteres.length;
    int[] quadro = new int[(qtdCaracters + 3) / 4];
    int index = 0;
    int desloca = 31;
    for (int i = 0; i < qtdCaracters; i++) {
      char caractere = mensagem.charAt(i);
      String caractere8Bits = String.format("%8s", Integer.toBinaryString(caractere)).replace(' ', '0');
      // System.out.println(caractere8Bits);
      for (int j = 0; j < 8; j++) {
        if (caractere8Bits.charAt(j) == '1') {
          quadro[index] = quadro[index] | (1 << desloca);
        }
        desloca--;
        if (desloca < 0) {
          desloca = 31;
          index++;
        }
      }
    }
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[1])).replace(' ', '0'));
    CamadaEnlaceDadosTransmissora(quadro);
    // CamadaFisicaTransmissora(quadro);
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissora.
   * Funcao: metodo para fazer apenas o enquadramendo por enquanto e chamar a
   * proxima camada
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  void CamadaEnlaceDadosTransmissora(int quadro[]) {
    // int[] quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramento(quadro);
    new Thread(() -> {
      try {
        CamadaEnlaceDadosTransmissoraEnquadramento(quadro);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }).start();
    // CamadaDeEnlaceTransmissoraControleDeErro(quadro);
    // CamadaDeEnlaceTransmissoraControleDeFluxo(quadro);
    // chama proxima camada
    // CamadaFisicaTransmissora(quadroEnquadrado);
  }// fim do metodo CamadaEnlaceDadosTransmissora

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramento.
   * Funcao: metodo para fazer o enquadramento com base no tipo de enquadramento
   * escolhido pelo usuario
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: nao retorna nada.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramento(int quadro[]) throws InterruptedException {

    // int quadroEnquadrado[] = new int[0]; // mudar depois
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);

        // try {
        // Platform.runLater(() -> {
        // try {
        // CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // });
        CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        break;
      case 1: // insercao de bytes
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
    }// fim do switch/case

    // return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres.
   * Funcao: metodo para fazer o enquadramento Contagem de Caracteres
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: nao retorna nada.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(int quadro[])
      throws InterruptedException {
    // int tamanhoQuadroEnquadrado = (int) Math.ceil((double) qtdCaracters / 3);
    // System.out.println(tamanhoQuadroEnquadrado);
    int[] quadroEnquadrado = new int[2];
    int indexQuadroEnquadrado = 0;
    int indexQuadro = 0;
    String contador4 = "00000100"; // represeta o 4 em binario, dessa forma, estou dizendo que vai contar de 4 em 4
    String contador3 = "00000011";
    String contador2 = "00000010";
    int deslocaQuadroEnquadrado = 31;
    int deslocaQuadro = 31;
    int indiceCaracter = 0;
    int qtdIteracao = (qtdCaracters * 8) + 8 * ((int) Math.ceil((double) qtdCaracters / 3));
    int qtdCaractersInseridos = 0;
    int qtdBitsInseridos = 0;
    int aux = 0;
    // System.out.println(qtdIteracao);

    for (int i = 0; i < qtdIteracao; i++) {
      if (deslocaQuadroEnquadrado >= 24) {
        int num;
        if (qtdCaracters % 3 == 2 && qtdCaractersInseridos + 2 >= qtdCaracters) {
          num = contador3.charAt(indiceCaracter); // muda a contagem para 3 caracteres, isto e, a informacao de controle
                                                  // mais 2 caracteres
          aux = 3;
        } else if (qtdCaracters % 3 == 1 && qtdCaractersInseridos + 1 >= qtdCaracters) {
          //
          aux = 2;
          num = contador2.charAt(indiceCaracter); // muda a contagem para 2 caracteres, isto e, a informacao de controle
                                                  // mais 1 caracter
        } else {
          aux = 4;
          //
          num = contador4.charAt(indiceCaracter);
        }
        if (num == '1') {
          quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
              | (1 << deslocaQuadroEnquadrado);
        }
        deslocaQuadroEnquadrado--;
        if (indiceCaracter >= 7) {
          indiceCaracter = 0;
        } else {
          indiceCaracter++;
        }
        qtdBitsTotais++;
      } else {
        // System.out.println("Vai pegar os bits de quadro");
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
              | (1 << deslocaQuadroEnquadrado);
        }
        deslocaQuadroEnquadrado--;

        qtdBitsTotais++;

        if (deslocaQuadroEnquadrado < 0 && aux == 4) {
          // envia quadro chamando o controle de erro;
          nextFrame();
          if (deslocaQuadroEnquadrado < 0) {
            deslocaQuadroEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          String next_frame_string = next_frame_to_send[indiceNextFrame];
          System.out.println(
              "Esse e a posicao do quadro: " + deslocaQuadroEnquadrado + " e esse e o index: " + indexQuadroEnquadrado);
          for (int j = 0; j < 2; j++) { // insere o numero de serie do quadro
            char numero = next_frame_string.charAt(j);
            if (numero == '1') {
              quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                  | (1 << deslocaQuadroEnquadrado);
            }
            deslocaQuadroEnquadrado--;
            qtdBitsTotais++;
            if (deslocaQuadroEnquadrado < 0) {
              deslocaQuadroEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
          }
          Principal.semaforoMeioDeComunicacao.acquire();

          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
          quadroEnquadrado = new int[2];
          deslocaQuadroEnquadrado = 31;
          indexQuadroEnquadrado = 0;
        } else if (deslocaQuadroEnquadrado == 7 && aux == 3) {
          // envia quadro chamando o controle de erro;
          nextFrame();
          String next_frame_string = next_frame_to_send[indiceNextFrame];
          System.out.println(
              "Esse e a posicao do quadro: " + deslocaQuadroEnquadrado + " e esse e o index: " + indexQuadroEnquadrado);
          for (int j = 0; j < 2; j++) { // insere o numero de serie do quadro
            char numero = next_frame_string.charAt(j);
            if (numero == '1') {
              quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                  | (1 << deslocaQuadroEnquadrado);
            }
            deslocaQuadroEnquadrado--;
            qtdBitsTotais++;
            if (deslocaQuadroEnquadrado < 0) {
              deslocaQuadroEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
          }
          Principal.semaforoMeioDeComunicacao.acquire();

          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado);
          quadroEnquadrado = new int[2];
        } else if (deslocaQuadroEnquadrado == 15 && aux == 2) {
          // envia quadro chamando o controle de erro;
          nextFrame();
          String next_frame_string = next_frame_to_send[indiceNextFrame];
          System.out.println(
              "Esse e a posicao do quadro: " + deslocaQuadroEnquadrado + " e esse e o index: " + indexQuadroEnquadrado);
          for (int j = 0; j < 2; j++) { // insere o numero de serie do quadro
            char numero = next_frame_string.charAt(j);
            if (numero == '1') {
              quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                  | (1 << deslocaQuadroEnquadrado);
            }
            deslocaQuadroEnquadrado--;
            qtdBitsTotais++;
            if (deslocaQuadroEnquadrado < 0) {
              deslocaQuadroEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
          }
          Principal.semaforoMeioDeComunicacao.acquire();

          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
          quadroEnquadrado = new int[2];
        }
        if (deslocaQuadroEnquadrado < 0) {
          deslocaQuadroEnquadrado = 31;
          indexQuadroEnquadrado++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
          // System.out.println("Mudou o index de quadro");
          if (indexQuadro >= quadro.length) {
            break;
          }
        }
      }
      qtdBitsInseridos++;
      if (qtdBitsInseridos == 8) {
        qtdBitsInseridos = 0;
        qtdCaractersInseridos++;
      }
    }
    /*
     * System.out.println("Esse e o quadroEnquadrado contagem de caracteres");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */

    // return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceDadosTransmissoraContagemDeCaracteres

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes.
   * Funcao: metodo para fazer o enquadramento Insercao de Bytes.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(int quadro[])
      throws InterruptedException {
    int[] quadroEnquadrado = new int[2];
    String flag = "00111111"; // minha flag sera a ? "01001000" seria H de Hugotoso
    String esc = "01000000"; // minha esc sera o @
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    String auxCompara = "";
    int indexQuadro = 0;
    int deslocaQuadro = 31;

    // insere o primeiro flag
    for (int i = 0; i < 8; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;
    // insere a informacao de controle ESC caso a mensagem tenha uma flag ou uma esc
    // fake, caso contrario, apenas insere o caracter
    for (int j = 0; j < qtdCaracters; j++) { // percorre a qtd de caracters
      // insere a flag do proximo caso ja tenha lido tres caractes
      if (j % 3 == 0 && j != 0) {
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = flag.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;

          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      }
      auxCompara = "";
      for (int i = 0; i < 8; i++) { // para cada caracter perroce os bits e adiciona em auxCompara
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          auxCompara = auxCompara + '1';
        } else {
          auxCompara = auxCompara + '0';
        }
        deslocaQuadro--;
        qtdBitsTotais++;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
          if (indexQuadro >= quadro.length) {
            break;
          }
        }
      }
      if (auxCompara.equals(flag) || auxCompara.equals(esc)) { // verifica se o caracter e igual a flag ou esc
        // System.out.println("Vai inserir o caracter fake");
        for (int i = 0; i < 8; i++) { // colocar o esc antes
          // System.out.println("Vai inserir o esc antes");
          char num = esc.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
        for (int i = 0; i < 8; i++) { // coloca o caracter depois
          char num = auxCompara.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
        }
        indiceCaracter = 0;
        if (indexQuadroEnquadrado >= quadroEnquadrado.length) {
          break;
        }
      } else {
        // System.out.println("Vai inserir o caracter normal");
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = auxCompara.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
        indiceCaracter = 0;
      }
      // verifica se ja leu tres caracteres ou se acabou de ler o ultimo, se sim,
      // adiciona a Flag
      if (j % 3 == 2 || j == qtdCaracters - 1) {
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = flag.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        // envia quadro chamando o controle de erro;
        Principal.semaforoMeioDeComunicacao.acquire();
        nextFrame();
        String next_frame_string = next_frame_to_send[indiceNextFrame];
        for (int k = 0; k < 2; k++) { // insere o numero de serie do quadro
          char numero = next_frame_string.charAt(k);
          if (numero == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          deslocaQuadoEnquadrado--;
          qtdBitsTotais++;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        quadroEnquadrado = new int[2];
        indexQuadroEnquadrado = 0;
        deslocaQuadoEnquadrado = 31;
      }
    } // fim da insercao dos esc
    /*
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */
    // return quadroEnquadrado;
    // Chama o controle de Erro
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits.
   * Funcao: metodo para fazer o enquadramento Insercao de Bits.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[])
      throws InterruptedException {
    int[] quadroEnquadrado = new int[2];
    String flag = "01111110";
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int contaBitsUm = 0;
    int contaCaracteres = 0;

    // insere o primeiro flag
    for (int i = 0; i < 8; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;

    for (int i = 0; i < 8 * qtdCaracters; i++) { // percorre o total de bits da qtd de caracters
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        contaBitsUm++;
        if (contaBitsUm == 6) {
          // inserir zero
          qtdBitsTotais++;
          // quadroEnquadrado[indexQuadroEnquadrado] =
          // quadroEnquadrado[indexQuadroEnquadrado] | (0 << deslocaQuadoEnquadrado);
          // //nao precisa inseri, basta pular pois o zero ja esta la
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          // zera o contador
          contaBitsUm = 0;
        }
        // depois inserir o bit
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      } else {
        contaBitsUm = 0;
      }
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }

      if ((i + 1) % 8 == 0) {
        contaCaracteres++;
      }
      // insere a flag a cada 3 caracters
      if ((i + 1) % 24 == 0 && contaCaracteres != qtdCaracters) { // se leu tres caracteres e o proximo nao e o utlimo,
                                                                  // entao insere flag do proximo tambem
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;

        }
        indiceCaracter = 0; // envia o quadro
        Principal.semaforoMeioDeComunicacao.acquire();
        nextFrame();
        String next_frame_string = next_frame_to_send[indiceNextFrame];
        for (int j = 0; j < 2; j++) { // insere o numero de serie do quadro
          char numero = next_frame_string.charAt(j);
          if (numero == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          deslocaQuadoEnquadrado--;
          qtdBitsTotais++;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        quadroEnquadrado = new int[2];
        indexQuadroEnquadrado = 0;
        deslocaQuadoEnquadrado = 31;
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      } else if (contaCaracteres == qtdCaracters) { // se chegou no ultimo caracter insere apenas uma flag
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        Principal.semaforoMeioDeComunicacao.acquire();
        nextFrame();
        String next_frame_string = next_frame_to_send[indiceNextFrame];
        for (int j = 0; j < 2; j++) { // insere o numero de serie do quadro
          char numero = next_frame_string.charAt(j);
          if (numero == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          deslocaQuadoEnquadrado--;
          qtdBitsTotais++;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        break;
      }
      deslocaQuadro--;
      qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }
    }
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica.
   * Funcao: metodo para fazer o enquadramento Violacao da Camada.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(int quadro[])
      throws InterruptedException {
    // Decodificacao realizada na Camada Fisica
    // Uma vez que ele viola a camada fisica, a decodificacao deve ser feita la
    // quando passamos o algoritmo de binario para manchester ou Manchester
    // Differencial
    // return quadro;
    // Chama o controle de Erro
    qtdBitsTotais = qtdCaracters * 8;
    CamadaFisicaTransmissora(quadro); // mudar depois
  }// fim do metodo CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica

  /*
   * ***************************************************************
   * Metodo: CamadaDeEnlaceTransmissoraControleDeErro.
   * Funcao: metodo para fazer o algoritmo de controle de erro com base no
   * escolhido pelo usuario
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void CamadaDeEnlaceTransmissoraControleDeErro(int[] quadroEnquadrado) throws InterruptedException {
    System.out.println("Essa e a qtd de bits totais: " + qtdBitsTotais);
    int[] quadroControleErro = new int[0]; // mudar depois o tamanho
    // Implementar logia de controle de erro
    switch (tipoControleErro) {
      case 0: // bit de paridade par
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(quadroEnquadrado);
        break;
      case 1: // bit de paridade impar
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(quadroEnquadrado);
        break;
      case 2: // CRC
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCRC(quadroEnquadrado);
        break;
      case 3: // codigo de Hamming
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(quadroEnquadrado);
        break;
    }// fim do switch/case
     // System.out.println("Essa e a mensagem a ser enviada");
     // for (int i = 0; i < quadroControleErro.length; i++){
     // System.out.println(String.format("%32s",
     // Integer.toBinaryString(quadroControleErro[i])).replace(' ', '0'));
     // }
    if (tipoDeEnquadramento != 3) { // se o enquadramento nao for violacao de camada, envia para a camada fisica
      // CamadaFisicaTransmissora(quadroControleErro);
      CamadaEnlaceDadosTransmissoraControleDeFluxo(quadroControleErro);
    } else { // se for violacao de camada, por ele ja ter passado pela camada fisica, envia
             // direto para o meio de comunicacao

      // System.out.println("ESSE E O QUADRO DA VIOLACAO DE CAMADA");
      // for (int i = 0; i < quadroControleErro.length; i++){
      // System.out.println(String.format("%32s",
      // Integer.toBinaryString(quadroControleErro[i])).replace(' ', '0'));
      // }

      Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
      Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
      Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
      Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
      Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
      Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
      Principal.meioDeComunicao.setSentidoOnda(0);
      qtdBitsTotais = 0;
      Principal.meioDeComunicao.meioDeComunicacao(quadroControleErro);
    }
  }// fim do medoto CamadaEnlaceDadosTransmissoraControleDeErro

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar.
   * Funcao: metodo para fazer o controle de erro bit de paridade par
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(int quadro[]) {
    // implementacao do algoritmo
    int[] quadroControleErro = new int[quadro.length * 2];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) { // percorre a quantidade de bits totais do quadro
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) { // conta a quantidade de bits 1
        qtdBitsUm++;
        quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
            | (1 << deslocaQuadroControleErro);
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for
    if (qtdBitsUm % 2 != 0) { // se a quantidade de 1 for impar, entao adiciona um 1 em quadroControleErro
      quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
          | (1 << deslocaQuadroControleErro);
    }
    qtdBitsTotais++;
    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadePar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar.
   * Funcao: metodo para fazer o controle de erro bit de paridade impar
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(int quadro[]) {
    // implementacao do algoritmo
    int[] quadroControleErro = new int[quadro.length * 2];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) { // percorre a quantidade de bits totais do quadro
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) { // conta a quantidade de bits 1
        qtdBitsUm++;
        quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
            | (1 << deslocaQuadroControleErro);
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for

    if (qtdBitsUm % 2 == 0) { // se a quantidade de 1 for par, entao adiciona um 1 em quadroControleErro
      quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
          | (1 << deslocaQuadroControleErro);
    }
    qtdBitsTotais++;
    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadeImpar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraControleDeErroCRC.
   * Funcao: metodo para fazer o controle de erro CRC
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCRC(int quadro[]) {
    // implementacao do algoritmo
    String crc32 = "100000100110000010001110110110111";
    char[] arrayDeCaracteres = crc32.toCharArray();
    int crc32Mascara = 0;
    int deslocaCRC32Mascara = 31;
    // cria a mascara crc32
    for (int i = 0; i < arrayDeCaracteres.length - 1; i++) {
      char caractere = crc32.charAt(i);
      if (caractere == '1') {
        crc32Mascara = crc32Mascara | (1 << deslocaCRC32Mascara);
      }
      deslocaCRC32Mascara--;
    } // terminou de criar a mascara do CRC32
    deslocaCRC32Mascara = 31;
    // System.out.println("CRC32");
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(crc32Mascara)).replace(' ', '0'));

    int[] quadroResto = new int[quadro.length * 2];
    int deslocaResto = 31;
    int indexResto = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    // coloca os bits de quadro em quadroResto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    indexResto = 0;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      int deslocaRestoAux = deslocaResto;
      int indexRestoAux = indexResto;
      int deslocaCRC32MascaraAux = 31; // Vamos percorrer do bit mais significativo ao menos significativo do CRC32

      if (bit == 1) {
        for (int j = 0; j < 32; j++) { // faz o XOR bit a bit
          // System.out.println("Desloca resto: " + deslocaRestoAux + "\nIndex resto aux:
          // " + indexRestoAux);
          // System.out.println("Fazendo o XOR");

          int bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
          // System.out.println("Bit do quadro: " + bitQuadroResto);

          int bitCRC = (crc32Mascara >> deslocaCRC32MascaraAux) & 1;
          // System.out.println("Bit do crc: " + bitCRC);

          int xorResult = bitQuadroResto ^ bitCRC;
          // System.out.println("XOR " + xorResult);

          // Limpa o bit na posição atual e insere o bit resultante do XOR
          quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
              | (xorResult << deslocaRestoAux);

          deslocaRestoAux--;
          deslocaCRC32MascaraAux--;

          if (deslocaRestoAux < 0) {
            deslocaRestoAux = 31;
            indexRestoAux++;
          }
          if (deslocaCRC32MascaraAux < 0) {
            // Fazer XOR com o bit implícito 1 do CRC-32
            bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
            xorResult = bitQuadroResto ^ 1; // XOR com 1
            quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
                | (xorResult << deslocaRestoAux);

            deslocaRestoAux--;
            if (deslocaRestoAux < 0) {
              deslocaRestoAux = 31;
              indexRestoAux++;
            }

            // Reseta o deslocamento da máscara do CRC para começar do bit mais
            // significativo novamente
            deslocaCRC32MascaraAux = 31;
          }
        }
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
    }

    // System.out.println("Esse é o quadro");
    // for (int i = 0; i < quadro.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }
    // System.out.println("Esse é o quadro com o Resto");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }
    deslocaQuadro = 31;
    indexQuadro = 0;
    deslocaResto = 31;
    indexResto = 0;
    // coloca os bits de quadro em quadroResto para enviar a mensagem com o resto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for
    qtdBitsTotais += 32; // acrescenta 32 bits pq o crc tem 33 bits
    // System.out.println(qtdBitsTotais);
    // System.out.println("Essa e a mensagem com o Resto no CRC");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }
    return quadroResto;

    // usar polinomio CRC-32(IEEE 802)
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming.
   * Funcao: metodo para fazer o controle de erro Codigo de Hamming
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(int quadro[]) {
    // implementacao do algoritmo // implementacao do algoritmo para VERIFICAR SE
    // HOUVE ERRO
    int[] quadroHamming = new int[quadro.length * 3];
    int indexQuadroHamming = 0;
    int deselocaQuadroHamming = 31;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int expoente = 0;
    // coloca os bits de quadro em quadroHamming pulando as posicoes onde e potencia
    // de 2
    for (int i = 0; i < qtdBitsTotais + 64; i++) {
      double potencia = Math.pow(2, expoente) - 1;
      if (i == (int) potencia) { // se a posicao for igual a uma potencia de 2 (menos 1, pq o array comeca de 0),
                                 // entao apenas pula a posicao
        expoente++;
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
      } else { // se nao, insere o bit de quadro na devida posicao de quadroHamming
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << deselocaQuadroHamming);
        }
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      }
      if (indexQuadro >= quadro.length || indexQuadroHamming >= quadroHamming.length) {
        break;
      }

    } // fim do for

    // System.out.println("Essa e o quadro Hamming sem a informacao de controle do
    // transmissor");
    // for (int i = 0; i < quadroHamming.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    // }

    indexQuadroHamming = 0;
    // deselocaQuadroHamming = 31;
    // inserir os valores dos bits de paridade
    for (int i = 0; i < 7; i++) {
      double posBitParidade = ((Math.pow(2, i)) - 1);
      // System.out.println("Pos bit paridade " + posBitParidade);
      int qtdUm = 0;
      deselocaQuadroHamming = 31 - (int) posBitParidade;
      // System.out.println("Desloca quadro hamming " + deselocaQuadroHamming);
      // System.out.println("Desloca quadro: " + deslocaQuadro);
      if (posBitParidade > 31) {
        deslocaQuadro = 0;
        indexQuadroHamming = 1;
      }
      int indexQuadroHammingAux = indexQuadroHamming;
      for (int j = 0; j < qtdBitsTotais * 4; j++) { // vai verificar todos os bits relacionados ao bit de paridade
        // System.out.println("Parte da posicao " + deselocaQuadroHamming);
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // conta
          int bit = (quadroHamming[indexQuadroHammingAux] >> deselocaQuadroHamming) & 1;
          if (bit == 1) {
            qtdUm++;
          }
          deselocaQuadroHamming--;
          if (deselocaQuadroHamming < 0) {
            deselocaQuadroHamming = 31 + deselocaQuadroHamming + 1;
            indexQuadroHammingAux++;
          }
          if (indexQuadroHammingAux >= quadroHamming.length) {
            break;
          }
        }
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // pula
          deselocaQuadroHamming--;
          if (deselocaQuadroHamming < 0) {
            deselocaQuadroHamming = 31 + deselocaQuadroHamming + 1;
            indexQuadroHammingAux++;
          }
          if (indexQuadroHammingAux >= quadroHamming.length) {
            break;
          }
        }
        // deselocaQuadroHamming -= i+1; //pula a quantidade necessaria para somar os
        // proximos bits
        // System.out.println("Pulou para " + deselocaQuadroHamming);
        // if (deselocaQuadroHamming < 0){
        // deselocaQuadroHamming = 31 + deselocaQuadroHamming + 1;
        // indexQuadroHammingAux++;
        // }
        if (indexQuadroHammingAux >= quadroHamming.length) {
          break;
        }
      }
      if (qtdUm % 2 == 1) { // insere o 1 na posicao do bit de paridade, pois a quantidade de 1 e impar
        // System.out.println("Inseriu a informacao de controle na posicao " +
        // (31-(int)posBitParidade));
        quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << 31 - (int) posBitParidade);
      }
    }
    qtdBitsTotais += 7; // adiciona 7 bits que sao os 7 bits de contorle
    // System.out.println("Essa e o quadro Hamming do transmissor");
    // for (int i = 0; i < quadroHamming.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    // }
    return quadroHamming;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErroCodigoDehamming

  public void CamadaEnlaceDadosTransmissoraControleDeFluxo(int quadro[]) throws InterruptedException {

    switch (tipoControleFluxo) {
      case 0:
        CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit(quadro);
        break;
      case 1:
        CamadaEnlaceDadosTransmissoraJanelaDeslizanteGoBackN(quadro);
        break;
      case 2:
        CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva(quadro);
        break;
    }// fim do switch/case

    // CamadaFisicaTransmissora(quadro);
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeFluxo

  public void CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit(int quadro[]) throws InterruptedException {
    System.out.println("Esse e o quadro no controle de fluxo");
    for (int i = 0; i < quadro.length; i++) {
      System.out.println(String.format("%32s",
          Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }
    int temporizador;
    if (tipoDeCodificacao != 0) { // da mais tempo caso a codificacao nao seja binaria
      temporizador = 8000;
    } else {
      temporizador = 4000;
    }
    int qtdBitsTotaisAnterior = qtdBitsTotais; //guarda a informacao da quantidade de bits totais
    Platform.runLater(() -> {
      try {
        CamadaFisicaTransmissora(quadro);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    try {
      Thread.sleep(temporizador); // TEMPO DE ESPERA
    } catch (InterruptedException e) {
    }
    if (indiceAck == -1) { // garante que o primeiro nao passe direto sem receber o ack
      indiceAck = 1;
    }
    while (next_frame_to_send[indiceNextFrame].charAt(1) != (ackJanela1Bit.charAt(1))) { // enquanto nao receber a confirmacao do receptor, fica reenviando indefinidamente
      try {
        qtdBitsTotais = qtdBitsTotaisAnterior;
        CamadaFisicaTransmissora(quadro);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        Thread.sleep(temporizador); // TEMPO DE ESPERA
      } catch (InterruptedException e) {
      }
    }
    // System.out.println("Quadro enviado!");

    // }).start(); // Fim thread
    // System.out.println("QUADRO FLUXO");
    // for (int i = 0; i < quadroControleFluxo.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroControleFluxo[i])).replace(' ', '0'));
    // }

  }// fim do metodo CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit

  public void CamadaEnlaceDadosTransmissoraJanelaDeslizanteGoBackN(int quadro[]) {
    // implementacao do algoritmo
  }// fim do metodo CamadaEnlaceDadosTransmissoraJanelaDeslizanteGoBackN

  public void CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva(int quadro[]) {
    // implementacao do algoritmo
  }// fim do CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva
  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissora.
   * Funcao: transforma o array de inteiros em outro array codificado com base na
   * tipo de codificacao e chama o MeioDeComunicacao.
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */

  public void CamadaFisicaTransmissora(int quadro[]) throws InterruptedException {
    // int[] fluxoBrutoDeBits = new int[0]; // ATENÇÃO: trabalhar com BITS!!!
    switch (tipoDeCodificacao) {
      case 0: // codificao binaria
        // qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        CamadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        // qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        CamadaFisicaTransmissoraCodificacaoManchester(quadro);
        // se o tipo do enquadramento for violacao, o calculo da qtdBitsTotais muda pois
        // quando adiciona informacao de controle, ela nao dobra pois nao passa pela
        // codificacao mancherter
        /*
         * if (tipoDeEnquadramento == 3) {
         * qtdBitsTotais = 2 * qtdBitsTotais - (qtdBitsTotais - qtdCaracters * 8);
         * } else {
         * qtdBitsTotais = 2 * qtdBitsTotais;
         * }
         */
        break;
      case 2: // codificacao manchester diferencial
        // qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        // se o tipo do enquadramento for violacao, o calculo da qtdBitsTotais muda pois
        // quando adiciona informacao de controle, ela nao dobra pois nao passa pela
        // codificacao mancherterDiferencial
        /*
         * if (tipoDeEnquadramento == 3) {
         * qtdBitsTotais = 2 * qtdBitsTotais - (qtdBitsTotais - qtdCaracters * 8);
         * } else {
         * qtdBitsTotais = 2 * qtdBitsTotais;
         * }
         */
        break;
    }// fim do switch/case
     // Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
     // Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
     // Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
     // Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
     // Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
     // Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
     // qtdBitsTotais = 0;
     // Principal.meioDeComunicao.meioDeComunicacao(fluxoBrutoDeBits);
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoBinaria.
   * Funcao: metodo para codificar a mensagem em binario.
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno
   */

  public void CamadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) {
    // implementacao do algoritmo
    /*
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
    Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
    Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
    Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
    Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
    Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
    Principal.meioDeComunicao.setSentidoOnda(0);
    qtdBitsTotais = 0;
    Principal.meioDeComunicao.meioDeComunicacao(quadro);
    // return quadro;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoBinaria

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoMancherster.
   * Funcao: metodo para codificar a mensagem em Mancherster (o O eh representado
   * por O1 e o 1 eh representado por 10).
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */

  public void CamadaFisicaTransmissoraCodificacaoManchester(int quadro[]) throws InterruptedException {
    int[] fluxoCodificacaoMancherster = new int[quadro.length * 2]; // (qtdCaracters+1)/2
    int deslocaFluxo = 31;
    int indexFLuxo = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;

    // System.out.println("Essa e a quantidade de bits totais ANTES: " +
    // qtdBitsTotais);
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (1 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (0 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
      } else {
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (0 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (1 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
      }
      deslocaQuadro--;
      // qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }
    }
    qtdBitsTotais = qtdBitsTotais * 2;
    // System.out.println("Esse é o QUADRO");
    // for (int i = 0; i < quadro.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }
    // System.out.println("Esse é o quadro codificado mancherster");
    // for (int i = 0; i < fluxoCodificacaoMancherster.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(fluxoCodificacaoMancherster[i])).replace(' ', '0'));
    // }
    // System.out.println("Essa e a quantidade de bits totais: " + qtdBitsTotais);

    // Verifica Se o enquadramento selecionado foi a violacao da camada fisica, caso
    // tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    // int quadroEnquadrado[];
    if (tipoDeEnquadramento == 3) { // se o tipo de enquadramento for violacao de camada fisica, chama a codificacao
                                    // violacao de camada fisica
      CodificacaoViolacaoCamadaFisica(fluxoCodificacaoMancherster);
    } else { // se nao, envia para o meio de comunicacao
      Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
      Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
      Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
      Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
      Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
      Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
      Principal.meioDeComunicao.setSentidoOnda(0);
      qtdBitsTotais = 0;
      Principal.meioDeComunicao.meioDeComunicacao(fluxoCodificacaoMancherster);
    }
    /*
     * System.out.println("Esse é o quadro codificado mancherster");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */
    // return quadroEnquadrado;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoManchester

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoManchersterDiferencial.
   * Funcao: metodo para codificar a mensagem em Mancherster (o O eh representado
   * por uma inversao de sinal e o 1 eh representado por uma falta de inversao de
   * sinal).
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */

  public void CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) throws InterruptedException {
    int[] fluxoCodificacaoManchersterDiferencial = new int[quadro.length * 2]; // (qtdCaracters+1)/2
    int deslocaFluxo = 31;
    int indexFLuxo = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int sinalAnterior = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        if (i % 24 == 0 && tipoDeEnquadramento == 3) { // faz isso pq na violacao de camada, quando for enviar um
                                                       // proximo quadro, o bit nao olhara para o anterior
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else if (i == 0) {
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else {
          // int num = deslocaFluxo + 1;
          // int sinalAnterior =
          // (fluxoCodificacaoManchersterDiferencial[indexFLuxo]>>(num))&1;
          if (sinalAnterior == 1) {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          } else {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          }
        }
      } else {
        if (i % 24 == 0 && tipoDeEnquadramento == 3) {// faz isso pq na violacao de camada, quando for enviar um proximo
                                                      // quadro, o bit nao olhara para o anterior
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else if (i == 0) {
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else {
          // int num = deslocaFluxo + 1;
          // int sinalAnterior =
          // (fluxoCodificacaoManchersterDiferencial[indexFLuxo]>>(num))&1;
          if (sinalAnterior == 1) {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          } else {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          }
        }
      }
      deslocaQuadro--;
      // qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }

    }
    qtdBitsTotais = qtdBitsTotais * 2;
    /*
     * System.out.println("Esse e o quadro codificado diferencial");
     * for (int i = 0; i < fluxoCodificacaoManchersterDiferencial.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(fluxoCodificacaoManchersterDiferencial[i])).replace('
     * ', '0'));
     * }
     */

    // Verifica Se o enquadramento selecionado foi a violacao da camada fisica, caso
    // tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    // int quadroEnquadrado[];
    if (tipoDeEnquadramento == 3) {
      CodificacaoViolacaoCamadaFisica(fluxoCodificacaoManchersterDiferencial);

    } else {
      // quadroEnquadrado = fluxoCodificacaoManchersterDiferencial;
      Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
      Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
      Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
      Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
      Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
      Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
      Principal.meioDeComunicao.setSentidoOnda(0);
      qtdBitsTotais = 0;
      Principal.meioDeComunicao.meioDeComunicacao(fluxoCodificacaoManchersterDiferencial);
    }

    // return quadroEnquadrado;
  }// fim do CamadaFisicaTransmissoraCodificacaoManchesterDiferencial

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica.
   * Funcao: metodo para fazer o enquadramento Violacao da Camada.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void CodificacaoViolacaoCamadaFisica(int[] quadro) throws InterruptedException {
    // int qtdFlags = 2 * ((int) Math.ceil((double) qtdCaracters / 3)); // calcula a
    // quantdidade de flags que sera inserida
    // int qtdBits = (8 * qtdCaracters * 2) + (qtdFlags * 2); // mudar para
    // qtdFlags*2 caso a flag seja 11
    // int tamanhoQuadroEnquadrado = qtdBits / 32 + 1;
    /*
     * if (qtdBits % 32 == 0){
     * tamanhoQuadroEnquadrado = qtdBits / 32; // Index do Array ENQUADRADO
     * }
     * else{
     * tamanhoQuadroEnquadrado = qtdBits / 32 + 1;
     * }
     */
    // Criando Novo quadro com o novo tamanhoint [] quadroEnquadrado = new
    // int[quadro.length*3];
    int[] quadroEnquadrado = new int[4];
    String flag = "11";
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    int indexQuadroFluxo = 0;
    int deslocaQuadroFluxo = 31;
    int contaCaracteres = 0;

    // insere o primeiro flag
    for (int i = 0; i < 2; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;

    for (int l = 0; l < 16 * qtdCaracters; l++) { // percorre o total de bits da qtd de caracters
      int bit = (quadro[indexQuadroFluxo] >> deslocaQuadroFluxo) & 1;
      // insere os bits de quadro em quadroEnquadrado
      if (bit == 1) {
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      if ((l + 1) % 16 == 0) {
        contaCaracteres++;
      }
      // insere a flag a cada 3 caracters
      if ((l + 1) % 48 == 0 && contaCaracteres != qtdCaracters) { // se leu tres caracteres e o proximo nao e o utlimo,
                                                                  // entao insere flag do proximo tambem
        for (int j = 0; j < 2; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        // envia quadro
        Principal.semaforoMeioDeComunicacao.acquire();

        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado);

        // terminou de enviar o quadro, cria um novo para enviar os proximos
        quadroEnquadrado = new int[4];
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado = 0;

        for (int j = 0; j < 2; j++) { // inserindo a flag do proximo
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      } else if (contaCaracteres == qtdCaracters) { // se chegou no ultimo caracter insere apenas uma flag
        for (int j = 0; j < 2; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        // envia quadro
        Principal.semaforoMeioDeComunicacao.acquire();

        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado);

        // terminou de enviar o quadro feito o controle de erro

        quadroEnquadrado = new int[2];
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado = 0;
      }
      deslocaQuadroFluxo--;
      qtdBitsTotais++;
      if (deslocaQuadroFluxo < 0) {
        deslocaQuadroFluxo = 31;
        indexQuadroFluxo++;
        if (indexQuadroFluxo >= quadro.length) {
          break;
        }
      }
    }
    /*
     * System.out.println("Esse e o quadroEnquadrado Violacao de Camada");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */

    // return quadroEnquadrado;
  }

  // ********************************************************************************************
  // ****************************** PARTE RECEPTORA NO TRANSMISSOR ******************************
  // ********************************************************************************************

  /* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 20/05/2023
* Ultima alteracao.: 31/05/2023
* Nome.............: Camada de Enlace de dados Controle de erro
* Funcao...........: Simular a camada enlace de dados de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextArea;
// import javafx.scene.input.MouseEvent;
// import javafx.scene.layout.VBox;
// import javafx.scene.text.Font;
// import javafx.stage.Popup;

// public class Receptor {
  private int tipoDeDecodificacao = 0;
  TextArea text = new TextArea();
  String buffer = "";
  int qtdBitsTotaisReceptorDoTransmissor = 0;
  boolean detectouErro = false;

  /*
   * ***************************************************************
   * Metodo: setTipoDeCodificacao.
   * Funcao: metodo para setar tipo de codificacao da mensagem.
   * Parametros: recebe uma quantidade de caracters do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoDeCodificacao(int codificacao) {
    this.tipoDeDecodificacao = codificacao;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoDeEnquadramento.
   * Funcao: metodo para setar tipo de enquadramento da mensagem.
   * Parametros: recebe o tipo do enquadramento.
   * Retorno: sem retorno.
   */
  public void setTipoDeEnquadramento(int tipoDeEnquadramento) {
    this.tipoDeEnquadramento = tipoDeEnquadramento;
  }

  /*
   * ***************************************************************
   * Metodo: zeraBuffer.
   * Funcao: metodo para zerar o buffer para a nova mensagem que chegar.
   * Parametros: rao recebe nada.
   * Retorno: sem retorno.
   */
  public void zeraBuffer() {
    this.buffer = "";
  }

  /*
   * ***************************************************************
   * Metodo: setQtdBitsTotais.
   * Funcao: metodo para inserir a quantidade de bits totais.
   * Parametros: recebe a quantidade de bits totais do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setQtdBitsTotais(int qtdBitsTotais) {
    this.qtdBitsTotais = qtdBitsTotais;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoControleErro.
   * Funcao: metodo para inserir o tipo de controle do erro.
   * Parametros: recebe a o tipo de controle do erro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoControleErro(int n) {
    this.tipoControleErro = n;
  }

  public void setTipoControleFluxo(int n) {
    this.tipoControleFluxo = n;
  }


  private String retiraNumDeSerieDoQuadro(int[] quadro) {
    String num = "";
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (i >= qtdBitsTotais - 2) { // pega os dois bits do numero de sequencia do quadro
        if (bit == 1) {
          num += '1';
        } else {
          num += '0';
        }
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
    }
    System.out.println("Esse e o numero de sequencia do quadro " + num);
    System.out.println("Esse e o quadro no num serie: ");
    for (int i = 0; i < quadro.length; i++) {
      System.out.println(String.format("%32s",
          Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }
    return num;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptora.
   * Funcao: metodo para chamar a codificacao necessaria para decodificar a
   * mensagem com base no tipo de codificacao e depois chamar a
   * CamadaDeAplicacaoReceptora.
   * Parametros: recebe um array do tipo inteiro referenete a mensagem codificada.
   * Retorno: sem retorno.
   */
  public void CamadaFisicaReceptora(int fluxoBrutoDeBits[]) {
    // se o tipo de enquadramento for violacao, entao primeiro vai chamar o controle
    // de erro, o controle de erro vai chamar a decodificacao fisica e a
    // decodificacao fisica vai chamar a camada de apresentacaos
    int[] quadro = new int[0]; // ATENÇÃO: trabalhar com BITS!!!
    if (tipoDeEnquadramento != 3) {
      switch (tipoDeDecodificacao) {
        case 0: // codificao binaria
          quadro = CamadaFisicaReceptoraDecodificacaoBinaria(fluxoBrutoDeBits);
          break;
        case 1: // codificacao manchester
          // if (tipoDeEnquadramento == 3) {
          // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
          // }
          quadro = CamadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
          break;
        case 2: // codificacao manchester diferencial
          // if (tipoDeEnquadramento == 3) {
          // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
          // }
          quadro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
          break;
      }// fim do switch/case
       // chama proxima camada
       // System.out.println("Esse é o quadro na camada fisica receptora
       // decodificado");
       // for (int i = 0; i < quadro.length; i++) {
       // System.out.println(String.format("%32s",
       // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
       // }
      CamadaEnlaceDadosReceptora(quadro);
    } else { // se o tipo de enquadramento for violacao de camada
      CamadaEnlaceDadosReceptora(fluxoBrutoDeBits);
    }
    // CamadaDeAplicacaoReceptora(quadro);
  }// fim do metodo CamadaFisicaTransmissora

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptora.
   * Funcao: metodo para chamar o controle de erro
   * Parametros: recebe um array do tipo inteiro referenete a mensagem
   * decodificada.
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptora(int quadro[]) {
    CamadaEnlaceDadosReceptoraControleDeErro(quadro);
    // CamadaEnlaceDadosReceptoraDesenquadramento(quadro);
    // CamadaEnlaceDadosReceptoraControleDeFluxo();
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErro.
   * Funcao: metodo para chamar o algoritmo de controle de erro para verificar se
   * detectou o erro
   * Parametros: recebe um array do tipo inteiro
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptoraControleDeErro(int quadro[]) {
    int[] quadroControleErro = new int[quadro.length];
    switch (tipoControleErro) {
      case 0: // bit de paridade par
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(quadro);
        break;
      case 1: // bit de paridade impar
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(quadro);
        break;
      case 2: // CRC
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroCRC(quadro);
        break;
      case 3: // codigo de hamming
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(quadro);
        break;
    }// fim do switch/case
    System.out.println("Essa e a qtd de bits totais tirando os bits de controle: " + qtdBitsTotais);
    if (detectouErro == false) { // se nao detectou erro, segue o fluxo
      System.out.println("Nao detectou erro");
      if (tipoDeEnquadramento != 3) {
        CamadaEnlaceDadosReceptoraDesenquadramento(quadroControleErro);
      } else {
        switch (tipoDeDecodificacao) {
          case 0: // codificao binaria
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoBinaria(quadroControleErro);
            break;
          case 1: // codificacao manchester
            if (tipoDeEnquadramento == 3) {
              quadroControleErro = DecodificacaoViolacaoCamadaFisica(quadroControleErro);
            }
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoManchester(quadroControleErro);
            break;
          case 2: // codificacao manchester diferencial
            if (tipoDeEnquadramento == 3) {
              quadroControleErro = DecodificacaoViolacaoCamadaFisica(quadroControleErro);
            }
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadroControleErro);
            break;
        }// fim do switch/case
        //CamadaEnlaceDadosReceptoraControleDeFluxo(quadroControleErro);
        // CamadaDeAplicacaoReceptora(quadroControleErro);
      }
    } else {
      System.out.print("Detectou erro");
    }

  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErro

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar.
   * Funcao: metodo para detectar o erro bit de paridade par
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro controle erro.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(int quadro[]) {
    // implementacao do algoritmo para VERIFICAR SE HOUVE ERRO
    int[] quadroControleErro = new int[quadro.length];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        if (i != qtdBitsTotais - 1) {
          quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
              | (1 << deslocaQuadroControleErro);
        }
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for
    if (qtdBitsUm % 2 == 0) { // se a quantiadade de bits 1 for par, significa que nao detectou erro
      detectouErro = false;
      // Mostrar na tela que nao detectou erro
    } else {
      detectouErro = true;
      // Mostrar na tela que detectou erro
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais--;

    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar.
   * Funcao: metodo para detectar o erro bit de paridade impar
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro controle erro.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(int quadro[]) {
    // implementacao do algoritmo para VERIFICAR SE HOUVE ERRO
    int[] quadroControleErro = new int[quadro.length];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        if (i != qtdBitsTotais - 1) {
          quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
              | (1 << deslocaQuadroControleErro);
        }
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for
    // System.out.println(qtdBitsUm);
    if (qtdBitsUm % 2 != 0) { // se a quantiadade de bits 1 for impar, significa que nao detectou erro
      detectouErro = false;
      // Mostrar na tela que nao detectou erro
    } else {
      detectouErro = true;
      // Mostrar na tela que detectou erro
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais--;

    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroCRC.
   * Funcao: metodo para detectar o erro CRC
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro resto.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroCRC(int quadro[]) {
    // implementacao do algoritmo
    String crc32 = "100000100110000010001110110110111";
    char[] arrayDeCaracteres = crc32.toCharArray();
    int crc32Mascara = 0;
    int deslocaCRC32Mascara = 31;
    // cria a mascara crc32
    for (int i = 0; i < arrayDeCaracteres.length - 1; i++) {
      char caractere = crc32.charAt(i);
      if (caractere == '1') {
        crc32Mascara = crc32Mascara | (1 << deslocaCRC32Mascara);
      }
      deslocaCRC32Mascara--;
    } // terminou de criar a mascara do CRC32
    deslocaCRC32Mascara = 31;
    // System.out.println("CRC32");
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(crc32Mascara)).replace(' ', '0'));

    int[] quadroResto = new int[quadro.length * 2];
    int deslocaResto = 31;
    int indexResto = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    // coloca os bits de quadro em quadroResto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    indexResto = 0;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // faz a divisao modulo 2 (XOR) para descobri o resto
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      int deslocaRestoAux = deslocaResto;
      int indexRestoAux = indexResto;
      int deslocaCRC32MascaraAux = 31; // Vamos percorrer do bit mais significativo ao menos significativo do CRC32

      if (bit == 1) {
        for (int j = 0; j < 32; j++) {
          // System.out.println("Desloca resto: " + deslocaRestoAux + "\nIndex resto aux:
          // " + indexRestoAux);
          // System.out.println("Fazendo o XOR");

          int bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
          // System.out.println("Bit do quadro: " + bitQuadroResto);

          int bitCRC = (crc32Mascara >> deslocaCRC32MascaraAux) & 1;
          // System.out.println("Bit do crc: " + bitCRC);

          int xorResult = bitQuadroResto ^ bitCRC;
          // System.out.println("XOR " + xorResult);

          // Limpa o bit na posição atual e insere o bit resultante do XOR
          quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
              | (xorResult << deslocaRestoAux);

          deslocaRestoAux--;
          deslocaCRC32MascaraAux--;

          if (deslocaRestoAux < 0) {
            deslocaRestoAux = 31;
            indexRestoAux++;
          }
          if (deslocaCRC32MascaraAux < 0) {
            // Fazer XOR com o bit implícito 1 do CRC-32
            bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
            xorResult = bitQuadroResto ^ 1; // XOR com 1
            quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
                | (xorResult << deslocaRestoAux);

            deslocaRestoAux--;
            if (deslocaRestoAux < 0) {
              deslocaRestoAux = 31;
              indexRestoAux++;
            }

            // Reseta o deslocamento da máscara do CRC para começar do bit mais
            // significativo novamente
            deslocaCRC32MascaraAux = 31;
          }
        }
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
    }

    qtdBitsTotais += 32;
    deslocaQuadro = 31;
    indexQuadro = 0;
    deslocaResto = 31;
    indexResto = 0;

    for (int i = 0; i < qtdBitsTotais; i++) { // verifica se o resto deu 0, senao deu e pq houve erro
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      if (bit == 1) {
        // System.out.println("\nDetectou o erro!\n");
        detectouErro = true;
        break;
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      if (indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    // System.out.println("Esse é o quadro Resto");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }

    qtdBitsTotais -= 64;
    // System.out.println(qtdBitsTotais);
    indexQuadro = 0;
    indexResto = 0;
    deslocaQuadro = 31;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // coloca os bits da carga util de quadro em quadroResto para enviar a
                                              // mensagem agora SEM o resto
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for
    // System.out.println("Essa e a mensagem SEM o Resto no CRC");
    // for (int i = 0; i < quadroResto.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }

    return quadroResto;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming.
   * Funcao: metodo para detectar o erro Codigo de Hamming
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro Hamming.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(int quadro[]) {
    // implementacao do algoritmo // implementacao do algoritmo para VERIFICAR SE
    // HOUVE ERRO
    int[] quadroHamming = new int[quadro.length * 3];
    int indexQuadroHamming = 0;
    int deselocaQuadroHamming = 31;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int expoente = 0;
    // System.out.println("Essa e o quadro com informacao de controle");
    // for (int i = 0; i < quadro.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }
    // deselocaQuadroHamming = 31;
    // verificar os valores dos bits de paridade
    for (int i = 0; i < 7; i++) {
      double posBitParidade = ((Math.pow(2, i)) - 1);
      // System.out.println("Pos bit paridade " + posBitParidade);
      int qtdUm = 0;
      deslocaQuadro = 31 - (int) posBitParidade;
      // System.out.println("Desloca quadro: " + deslocaQuadro);
      if (posBitParidade > 31) {
        deslocaQuadro = 0;
        indexQuadro = 1;
      }
      // System.out.println("Desloca quadro hamming " + deselocaQuadroHamming);
      int indexQuadroAux = indexQuadro;
      for (int j = 0; j < qtdBitsTotais * 4; j++) { // vai verificar todos os bits relacionados ao bit de paridade
        // System.out.println("Parte da posicao " + deslocaQuadro);
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // conta
          int bit = (quadro[indexQuadroAux] >> deslocaQuadro) & 1;
          // System.out.println(bit);
          if (bit == 1) {
            qtdUm++;
          }
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31 + deslocaQuadro + 1;
            indexQuadroAux++;
          }
          if (indexQuadroAux >= quadro.length) {
            break;
          }
        }
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // pula
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31 + deslocaQuadro + 1;
            indexQuadroAux++;
          }
          if (indexQuadroAux >= quadro.length) {
            break;
          }
        }
        // deselocaQuadroHamming -= i+1; //pula a quantidade necessaria para somar os
        // proximos bits
        // System.out.println("Pulou para " + deselocaQuadroHamming);
        // if (deselocaQuadroHamming < 0){
        // deselocaQuadroHamming = 31 + deselocaQuadroHamming + 1;
        // indexQuadroHammingAux++;
        // }
        if (indexQuadroAux >= quadro.length) {
          break;
        }
      }
      if (qtdUm % 2 == 1) { // se a quantidadde de 1 (incluindo a informacao de controle) nao for par, entao
                            // deu erro
        // System.out.println("\n\nDetectou ERRO! na iteracao " + i + "\n\n");
        detectouErro = true;
      }
      // System.out.println(qtdUm);
    }

    // retira os bits de informacao de controle de quadroHamming
    indexQuadro = 0;
    indexQuadroHamming = 0;
    deslocaQuadro = 31;
    deselocaQuadroHamming = 31;
    for (int i = 0; i < qtdBitsTotais + 64; i++) {
      double potencia = Math.pow(2, expoente) - 1;
      if (i == (int) potencia) { // se a posicao for igual a uma potencia de 2 (menos 1, pq o array comeca de 0),
                                 // entao apenas pula a posicao
        expoente++;
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      } else { // se nao, insere o bit de quadro na devida posicao de quadroHamming
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << deselocaQuadroHamming);
        }
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      }
      if (indexQuadro >= quadro.length || indexQuadroHamming >= quadroHamming.length) {
        break;
      }
    } // fim do for
    // System.out.println("Essa e o quadro Hamming");
    // for (int i = 0; i < quadroHamming.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    // }
    qtdBitsTotais -= 7;
    return quadroHamming;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramento.
   * Funcao: metodo para fazer o desenquadramento com base no escolhido pelo
   * usuario
   * Parametros: recebe um array do tipo inteiro
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptoraDesenquadramento(int quadro[]) {
    int quadroDesenquadrado[] = new int[0]; // mudar depoi
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroDesenquadrado = quadro;
        break;
    }// fim do switch/case
    CamadaDeAplicacaoReceptora(quadroDesenquadrado);
    //CamadaEnlaceDadosReceptoraControleDeFluxo(quadroDesenquadrado);

  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria.
   * Funcao: metodo para decodificar a mensagem binaria.
   * Parametros: recebe o array de inteiros.
   * Retorno: retorna um array de interios decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int fluxoBrutoDeBits[]) {
    int[] quadro = new int[fluxoBrutoDeBits.length];
    quadro = fluxoBrutoDeBits;
    /*
     * int indexQuadro = 0;
     * for (int i = 0; i < fluxoBrutoDeBits.length; i++){
     * for (int j = 31; j >= 0; j--){
     * int bit = (fluxoBrutoDeBits[i] >> j) & 1;
     * quadro[indexQuadro] = quadro[indexQuadro] | (bit << j);
     * }
     * indexQuadro++;
     * }
     */
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoMancherster.
   * Funcao: metodo para decodificar a mensagem em Mancherster (cada par O1
   * representa o 0 e cada para 10 representa o 1).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int fluxoBrutoDeBits[]) {
    int[] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // qtdBitsTotais
      int posAnterior = deslocaFluxo;
      int posSucessor = deslocaFluxo - 1;
      int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
      int bitSucessor = (fluxoBrutoDeBits[indexFLuxo] >> posSucessor) & 1;
      if (bitAnterior == 1 && bitSucessor == 0) {
        quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 0 && bitSucessor == 1) {
        quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 1 && bitSucessor == 1) { // igora o par 11 caso o enquadramento seja violacao de camada
        i++;
        /*
         * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
         * deslocaQuadro--;
         * if (deslocaQuadro < 0){
         * deslocaQuadro = 31;
         * indexQuadro++;
         * }
         * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
         */
      }
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      deslocaFluxo -= 2;
      if (deslocaFluxo < 0) {
        deslocaFluxo = 31;
        indexFLuxo++;
      }
      if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadro.length) {
        break;
      }

    }
    /*
     * System.out.println("Esse e o quadro decodificado Mancherster");
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    qtdBitsTotais = qtdBitsTotais / 2;
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptorarDecodificacaoManchersterDiferencial.
   * Funcao: metodo para decodificar a mensagem em ManchersterDiferencial (onda a
   * inversao de sinal representa o 0 e a falta de inversao de sinall representa o
   * 1).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int fluxoBrutoDeBits[]) {
    // o problema acontece quando ele le uma sequencia de 1111 1 que seria a flag e
    // o proximo bit
    int[] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFluxo = 0;
    int deslocaFluxo = 29;
    int primeiroBit = (fluxoBrutoDeBits[0] >> 31) & 1;
    int segundoBit = (fluxoBrutoDeBits[0] >> 30) & 1;
    // if (tipoDeEnquadramento == 3) {
    // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
    // }
    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) { // qtdBitsTotais
      if (i == 0) { // se esta na primeira iteracao
        if (primeiroBit == 1 && segundoBit == 0) {
          quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          deslocaQuadro--;
        } else if (primeiroBit == 0 && segundoBit == 1) {
          quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
          deslocaQuadro--;
        } else if (primeiroBit == 1 && segundoBit == 1) {
          i++;
          /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (((fluxoBrutoDeBits[0] >> 29) & 1)==1 & ((fluxoBrutoDeBits[0] >> 28) & 1)
           * == 0){
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * }
           * else if (((fluxoBrutoDeBits[0] >> 29) & 1)==0 & ((fluxoBrutoDeBits[0] >> 28)
           * & 1) == 1){
           * quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro);
           * }
           */
        }
      } else {
        int posAnterior = deslocaFluxo + 1;
        int posSucessor = deslocaFluxo;
        int proximo = deslocaFluxo - 1;
        int bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[indexFluxo] >> posSucessor) & 1;
        int bitProximo = (fluxoBrutoDeBits[indexFluxo] >> proximo) & 1;
        if (bitSucessor == 1 && bitProximo == 1) { // verifica se achou o par 11 (serve para a violacao de camada)
          i++;
          /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (deslocaQuadro < 0){
           * deslocaQuadro = 31;
           * indexQuadro++;
           * }
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           */
        } else if (bitSucessor == 0 && bitProximo == 0) {
          // deslocaQuadro--;
        } else if ((bitAnterior == bitSucessor)) {
          quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          deslocaQuadro--;
        } else if (bitAnterior != bitSucessor) {
          quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
          deslocaQuadro--;
        }
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
        deslocaFluxo -= 2;
        if (deslocaFluxo <= 0) {
          posAnterior = 0;
          bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
          indexFluxo++;
          if (indexFluxo >= fluxoBrutoDeBits.length) {
            break;
          }
          posSucessor = 31;
          bitSucessor = (fluxoBrutoDeBits[indexFluxo] >> posSucessor) & 1;
          proximo = 30;
          bitProximo = (fluxoBrutoDeBits[indexFluxo] >> proximo) & 1;
          /*
           * if (bitSucessor == 1 && bitProximo == 1){ //verifica se achou o par 11 (serve
           * para a violacao de camada)
           * i++;
           * /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (deslocaQuadro < 0){
           * deslocaQuadro = 31;
           * indexQuadro++;
           * }
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * 
           * }
           * else
           */if (bitSucessor == 0 && bitProximo == 0) {
            // deslocaQuadro--;
          } else if ((bitAnterior == bitSucessor)) {
            quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            deslocaQuadro--;
          } else if (bitAnterior != bitSucessor) {
            quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
            deslocaQuadro--;
          }
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaFluxo = 29;
        }
        if (indexFluxo >= fluxoBrutoDeBits.length | indexQuadro >= quadro.length) {
          break;
        }
      }
    }
    /*
     * System.out.println("Esse e o quadro descodificado Mancherster Diferencial");
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    qtdBitsTotais = qtdBitsTotais / 2;
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres.
   * Funcao: metodo para desenquadrar a mensagem do tipo Contagem de Caracteres.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    String contador4 = "00000100"; // represeta o 4 em binario, dessa forma, estou dizendo que vai contar de 4 em 4
    String contador3 = "00000011"; // representa o 3
    String contador2 = "00000010"; // representa o 2
    String aux = "";
    int qtdProximaIteracao = 0;
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (bit == 1) {
        aux += '1';
      } else {
        aux += '0';
      }
      if (aux.equals(contador4)) {
        qtdProximaIteracao = 24;
      } else if (aux.equals(contador3)) {
        qtdProximaIteracao = 16;
      } else if (aux.equals(contador2)) {
        qtdProximaIteracao = 8;
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        for (int j = 0; j < qtdProximaIteracao; j++) {
          bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
          if (bit == 1) {
            quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          }
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
            break;
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes.
   * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bytes.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    String flag = "00111111";
    String esc = "01000000";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1) {
        aux += '1';
      } else {
        aux += '0';
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        if (aux.equals(esc)) { // insere os proximos 8 bits em quadro
          for (int j = 0; j < 8; j++) {
            bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
            if (bit == 1) {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            }
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado < 0) {
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        } else if (aux.equals(flag)) { // ignora
        } else if (aux.equals("00000000")) {
          break;
        } else {
          for (int j = 0; j < 8; j++) {
            if (aux.charAt(j) == '1') {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            } /*
               * deslocaQuadoEnquadrado--;
               * if (deslocaQuadoEnquadrado<0){
               * deslocaQuadoEnquadrado = 31;
               * indexQuadroEnquadrado++;
               * }
               */
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits.
   * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bits.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int qtdBitsUm = 0;
    String flag = "01111110";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1) {
        aux += '1';
        qtdBitsUm++;
        bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
        if (qtdBitsUm == 5 && bit == 0) {
          qtdBitsUm = 0;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
      } else {
        aux += '0';
        qtdBitsUm = 0;
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        if (aux.equals(flag)) {
          // ignora
        } else if (aux.equals("00000000")) {
          break;
        } else {
          for (int j = 0; j < 8; j++) {
            if (aux.charAt(j) == '1') {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            } /*
               * deslocaQuadoEnquadrado--;
               * if (deslocaQuadoEnquadrado<0){
               * deslocaQuadoEnquadrado = 31;
               * indexQuadroEnquadrado++;
               * }
               */
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: DecodificacaoViolacaoCamadaFisica.
   * Funcao: metodo para desenquadrar a mensagem do tipo Violacao de Camada.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] DecodificacaoViolacaoCamadaFisica(int[] fluxoBrutoDeBits) {
    int[] quadroDesenquadrado = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) { // qtdBitsTotais
      int posAnterior = deslocaFluxo;
      int posSucessor = deslocaFluxo - 1;
      int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
      int bitSucessor = (fluxoBrutoDeBits[indexFLuxo] >> posSucessor) & 1;
      if (bitAnterior == 1 && bitSucessor == 0) {
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 0 && bitSucessor == 1) {
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 1 && bitSucessor == 1) { // igora o par 11 caso o enquadramento seja violacao de camada
        i++;
      } else {
        break;
      }
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      deslocaFluxo -= 2;
      if (deslocaFluxo < 0) {
        deslocaFluxo = 31;
        indexFLuxo++;
      }
      if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadroDesenquadrado.length) {
        break;
      }
    }
    /*
     * System.out.println("Esse e o quadroDesenquadrado Violacao de camada");
     * for (int i = 0; i < quadroDesenquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroDesenquadrado[i])).replace(' ', '0'));
     * }
     */
    return quadroDesenquadrado;
  }
  /*
   * ***************************************************************
   * Metodo: CamadaDeAplicacaoReceptora.
   * Funcao: metodo para trasnformar o array de interos decodificado em mensagem e
   * chamar a AplicacaoReceptora.
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */
  public void CamadaDeAplicacaoReceptora(int quadro[]) {

    // for (int i = 0; i < quadro.length; i++){
    // System.out.println("Esse e o quadro CamadaDeAplicacaoReceptora "+String.
    // format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }

    String mensagem = "";
    // String representando a sequência de bits
    String binaryString = "";
    int contador = 0;
    if (quadro != null) { //se houve erro ou o quadro foi duplicado, ignora
      for (int i = 0; i < quadro.length; i++) {
        for (int j = 31; j >= 0; j--) {
          int bit = (quadro[i] >> j) & 1;
          if (bit == 1) {
            binaryString += "1";
          } else {
            binaryString += "0";
          }
          contador += 1;
          if (contador == 8) {
            int intValue = Integer.parseInt(binaryString, 2);
            mensagem += (char) intValue;
            binaryString = "";
            contador = 0;
          }
        }
      }
      // System.out.println("Essa é a mensagem:" + mensagem);
      // chama proxima camada
      buffer += mensagem;
      ackJanela1Bit = buffer;

      //AplicacaoReceptora(buffer);
    }

  }// fim do metodo CamadaDeAplicacaoReceptora




}