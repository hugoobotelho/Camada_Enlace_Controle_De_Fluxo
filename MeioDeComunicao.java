/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 20/05/2023
* Ultima alteracao.: 31/05/2023
* Nome.............: Camada de Enlace de dados Controle de erro
* Funcao...........: Simular a camada enlace de dados de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import java.util.Arrays;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MeioDeComunicao {
  // private Receptor receptor = new Receptor();
  private int tipoDeCodificacao = 0;
  private int tipoDeEnquadramento = 0;
  private int qtdBitsTotais = 0;
  private int qtdBitsFluxo = 0;
  private int tipoControleErro = 0;
  private int porcentagemErro = 0;
  private int qtdBitsErrados = 0;
  private int sentido = 0; // testando
  public boolean erro = false;

  /*
   * ***************************************************************
   * Metodo: setTipoDeCodificacao.
   * Funcao: metodo para setar tipo de codificacao da mensagem.
   * Parametros: recebe o tipo de codificacao do tipo inteiro.
   * Retorno: sem retorno.
   */

  public void setTipoDeCodificacao(int tipoDeCodificacao) {
    this.tipoDeCodificacao = tipoDeCodificacao;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoDeEnquadramento.
   * Funcao: metodo para setar tipo de enquadramento da mensagem.
   * Parametros: recebe o tipo de enquadramento do tipo Inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoDeEnquadramento(int tipoDeEnquadramento) {
    this.tipoDeEnquadramento = tipoDeEnquadramento;
  }

  /*
   * ***************************************************************
   * Metodo: setQtdBitsTotais.
   * Funcao: metodo para setar a quantidade de bits da mensagem.
   * Parametros: recebe a quantidade de bits totais do tipo Inteiro.
   * Retorno: sem retorno.
   */
  public void setQtdBitsTotais(int qtdBitsTotais) {
    this.qtdBitsTotais = qtdBitsTotais;
    this.qtdBitsFluxo = qtdBitsTotais;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoControleErro.
   * Funcao: metodo para inserir o tipo de controle de erro.
   * Parametros: recebe o tipo de controle de erro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoControleErro(int tipoControleErro) {
    this.tipoControleErro = tipoControleErro;
  }

  /*
   * ***************************************************************
   * Metodo: setPorcentagemErro.
   * Funcao: metodo para inserir a porcentagem de ocorrer o erro.
   * Parametros: recebe a porcentagem do erro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setPorcentagemErro(int porcentagemErro) {
    this.porcentagemErro = porcentagemErro;
  }

  /*
   * ***************************************************************
   * Metodo: setQtdBistErrados.
   * Funcao: metodo para inserir a quantidade de bits que serao alterados.
   * Parametros: recebe a quantidade de bits totais do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setQtdBistErrados(int qtdBitsErrados) {
    this.qtdBitsErrados = qtdBitsErrados;
  }

  public void setSentidoOnda(int n) {
    this.sentido = n;
  }

  /*
   * ***************************************************************
   * Metodo: meioDeComunicacao.
   * Funcao: faz com que a forma de onda apareca na tela e passa os bits da
   * mensagem para a CamadaFisicaReceptora.
   * Parametros: recebe o array de inteiros que representa a mensagem codificada.
   * Retorno: sem retorno.
   */
  public void meioDeComunicacao(int fluxoBrutoDeBits[]) {

    Principal.receptor.setTipoDeCodificacao(tipoDeCodificacao);
    Principal.receptor.setTipoDeEnquadramento(tipoDeEnquadramento);
    Principal.receptor.setQtdBitsTotais(qtdBitsTotais);
    Principal.receptor.setTipoControleErro(tipoControleErro);

    Image b1Image = new Image("/img/line_horizontal.png");
    ImageView b1 = new ImageView(b1Image);
    b1.setLayoutX(240);
    b1.setLayoutY(360);
    Image a1Image = new Image("/img/line_horizontal.png");
    ImageView a1 = new ImageView(a1Image);
    a1.setLayoutX(240);
    a1.setLayoutY(260);
    Image b2Image = new Image("/img/line_horizontal.png");
    ImageView b2 = new ImageView(b2Image);
    b2.setLayoutX(340);
    b2.setLayoutY(360);
    Image a2Image = new Image("/img/line_horizontal.png");
    ImageView a2 = new ImageView(a2Image);
    a2.setLayoutX(340);
    a2.setLayoutY(260);
    Image b3Image = new Image("/img/line_horizontal.png");
    ImageView b3 = new ImageView(b3Image);
    b3.setLayoutX(440);
    b3.setLayoutY(360);
    Image a3Image = new Image("/img/line_horizontal.png");
    ImageView a3 = new ImageView(a3Image);
    a3.setLayoutX(440);
    a3.setLayoutY(260);
    Image b4Image = new Image("/img/line_horizontal.png");
    ImageView b4 = new ImageView(b4Image);
    b4.setLayoutX(540);
    b4.setLayoutY(360);
    Image a4Image = new Image("/img/line_horizontal.png");
    ImageView a4 = new ImageView(a4Image);
    a4.setLayoutX(540);
    a4.setLayoutY(260);
    Image b5Image = new Image("/img/line_horizontal.png");
    ImageView b5 = new ImageView(b5Image);
    b5.setLayoutX(640);
    b5.setLayoutY(360);
    Image a5Image = new Image("/img/line_horizontal.png");
    ImageView a5 = new ImageView(a5Image);
    a5.setLayoutX(640);
    a5.setLayoutY(260);
    Image b6Image = new Image("/img/line_horizontal.png");
    ImageView b6 = new ImageView(b6Image);
    b6.setLayoutX(740);
    b6.setLayoutY(360);
    Image a6Image = new Image("/img/line_horizontal.png");
    ImageView a6 = new ImageView(a6Image);
    a6.setLayoutX(740);
    a6.setLayoutY(260);

    Image v1Image = new Image("/img/line_vertical.png");
    ImageView v1 = new ImageView(v1Image);
    v1.setLayoutX(335);
    v1.setLayoutY(260);
    Image v2Image = new Image("/img/line_vertical.png");
    ImageView v2 = new ImageView(v2Image);
    v2.setLayoutX(435);
    v2.setLayoutY(260);
    Image v3Image = new Image("/img/line_vertical.png");
    ImageView v3 = new ImageView(v3Image);
    v3.setLayoutX(535);
    v3.setLayoutY(260);
    Image v4Image = new Image("/img/line_vertical.png");
    ImageView v4 = new ImageView(v4Image);
    v4.setLayoutX(635);
    v4.setLayoutY(260);
    Image v5Image = new Image("/img/line_vertical.png");
    ImageView v5 = new ImageView(v5Image);
    v5.setLayoutX(735);
    v5.setLayoutY(260);

    ImageView[] arraySinaisAltos = { a1, a2, a3, a4, a5, a6 };
    ImageView[] arraySinaisBaixos = { b1, b2, b3, b4, b5, b6 };
    ImageView[] arrayTransicoes = { v1, v2, v3, v4, v5 };
    Platform.runLater(() -> {
      for (int i = 0; i < 6; i++) {
        arraySinaisAltos[i].setVisible(false);
        arraySinaisBaixos[i].setVisible(false);
      }
      for (int i = 0; i < 5; i++) {
        arrayTransicoes[i].setVisible(false);
      }
    });
    Platform.runLater(() -> {
      Principal.root.getChildren().addAll(b1, a1, b2, a2, b3, a3, b4, a4, b5, a5, b6, a6, v1, v2, v3, v4, v5);
    });
    new Thread(() -> {

      int[] fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
      int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length];
      int deslocaFluxoPontoA = 31;
      int indexFluxoPontoA = 0;
      int deslocaFluxoPontoB = 31;
      int indexFluxoPontoB = 0;
      Random random = new Random();
      // Gera um número aleatório entre 0 e 10
      double numeroAleatorio = random.nextDouble() * 10; // Multiplica por 100 para ficar na escala de 0 a 100

      // Verifica se o número aleatório está dentro da probabilidade fornecida
      if (numeroAleatorio <= porcentagemErro && numeroAleatorio != 0) {
        // System.out.println("Deu erro");
        erro = true;
      } else {
        // System.out.println("Nao deu erro");
        erro = false;
      }
      Random bitsAleatorios = new Random();
      int[] posBitAleatorio = new int[qtdBitsErrados];
      // Gera a quantidade especificada de números aleatórios
      for (int i = 0; i < qtdBitsErrados; i++) {
        int pos = bitsAleatorios.nextInt(qtdBitsFluxo); // Gera um número aleatório entre 0 e qtdBitsFluxo

        boolean encontrado;
        do {
          encontrado = false;
          for (int j = 0; j < qtdBitsErrados; j++) {
            if (posBitAleatorio[j] == pos) {
              encontrado = true;
              pos = bitsAleatorios.nextInt(qtdBitsFluxo); // Gera um novo número se já estiver presente
              break;
            }
          }
        } while (encontrado);

        posBitAleatorio[i] = pos;
      }

      for (int i = 0; i < qtdBitsFluxo; i++) {
        int bit = (fluxoBrutoDeBitsPontoA[indexFluxoPontoA] >> deslocaFluxoPontoA) & 1;
        if (erro) { // verifica se houve erro
          for (int j = 0; j < posBitAleatorio.length; j++) {
            if (i == posBitAleatorio[j]) { // altera o bit na posicao aleatoria
              if (bit == 1) { // caso o bit seja 1, inverte o valor para 0
                bit = 0;
              } else { // se o bit for 0, inverto o valor para 1
                bit = 1;
              }
            }
          }
        }
        if (bit == 1) {
          fluxoBrutoDeBitsPontoB[indexFluxoPontoB] = fluxoBrutoDeBitsPontoB[indexFluxoPontoB]
              | (1 << deslocaFluxoPontoB);
        }
        deslocaFluxoPontoB--;
        if (deslocaFluxoPontoB < 0) {
          deslocaFluxoPontoB = 31;
          indexFluxoPontoB++;
        }
        deslocaFluxoPontoA--;
        if (deslocaFluxoPontoA < 0) {
          deslocaFluxoPontoA = 31;
          indexFluxoPontoA++;
        }
        // System.out.println("Mudou o index de quadro");
        if (indexFluxoPontoA >= fluxoBrutoDeBitsPontoA.length || indexFluxoPontoB >= fluxoBrutoDeBitsPontoB.length) {
          break;
        }
      } // fim do for
      //fluxoBrutoDeBitsPontoB[fluxoBrutoDeBitsPontoB.length-1] = fluxoBrutoDeBitsPontoA[fluxoBrutoDeBitsPontoA.length-1];
      // chama proxima camada
      System.out.println("Esse é o quadro no com informacao de controle no meio");
      for (int i = 0; i < fluxoBrutoDeBitsPontoB.length; i++){
      System.out.println(String.format("%32s",
      Integer.toBinaryString(fluxoBrutoDeBitsPontoB[i])).replace(' ', '0'));
      }

      if (sentido == 0) { // se o sentido e igual a 0, entao a infromacao vai sair da esquerda para a
                          // direita
        for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
          for (int j = 31; j >= 0; j--) {
            int bit = (fluxoBrutoDeBits[i] >> j) & 1;
            if (bit == 1) {
              Platform.runLater(() -> {
                arraySinaisAltos[0].setVisible(true);
                arraySinaisBaixos[0].setVisible(false);
                if (/* !arraySinaisAltos[1].isVisible()|| */arraySinaisBaixos[1].isVisible()) {
                  arrayTransicoes[0].setVisible(true);
                } else {
                  arrayTransicoes[0].setVisible(false);
                }
              });
            } else {
              Platform.runLater(() -> {
                arraySinaisAltos[0].setVisible(false);
                arraySinaisBaixos[0].setVisible(true);
                if (/* !arraySinaisBaixos[1].isVisible()|| */arraySinaisAltos[1].isVisible()) {
                  arrayTransicoes[0].setVisible(true);
                } else {
                  arrayTransicoes[0].setVisible(false);
                }
              });
            }
            try {
              Thread.sleep(50); // Adiciona um pequeno intervalo entre as iteracoes ALTERAR DEPOIS!!!
            } catch (InterruptedException e) {
              // e.printStackTrace();
            }

            // Atualiza a onda
            Platform.runLater(() -> {
              for (int k = 5; k > 0; k--) {
                if (arraySinaisAltos[k - 1].isVisible()) {
                  arraySinaisAltos[k].setVisible(true);
                  arraySinaisBaixos[k].setVisible(false);
                } else if (arraySinaisBaixos[k - 1].isVisible()) {
                  arraySinaisAltos[k].setVisible(false);
                  arraySinaisBaixos[k].setVisible(true);
                }
              }
              for (int k = 5; k > 0; k--) {
                if (((arraySinaisAltos[k - 1].isVisible() && !arraySinaisAltos[k].isVisible())
                    || (!arraySinaisAltos[k - 1].isVisible() && arraySinaisAltos[k].isVisible()))
                    && ((arraySinaisBaixos[k - 1].isVisible() && !arraySinaisBaixos[k].isVisible())
                        || (!arraySinaisBaixos[k - 1].isVisible() && arraySinaisBaixos[k].isVisible()))) {
                  arrayTransicoes[k - 1].setVisible(true);
                } else {
                  arrayTransicoes[k - 1].setVisible(false);
                }
              }
            });
            qtdBitsTotais--;
            if (qtdBitsTotais <= 0) {
              break;
            }
          }
          if (qtdBitsTotais <= 0) {
            break;
          }
        }
        Platform.runLater(() -> {
          arraySinaisAltos[0].setVisible(false);
          arraySinaisBaixos[0].setVisible(false);
        });
        try {
          Thread.sleep(150); // Adiciona um pequeno intervalo entre as iteracoes, ALTERAR DEPOIS!!!
        } catch (InterruptedException e) {
          // e.printStackTrace();
        }
        for (int i = 5; i > 0; i--) {
          Platform.runLater(() -> {
            for (int j = 5; j > 0; j--) {
              if (arraySinaisAltos[j - 1].isVisible()) {
                arraySinaisAltos[j].setVisible(true);
                arraySinaisBaixos[j].setVisible(false);
              } else if (arraySinaisBaixos[j - 1].isVisible()) {
                arraySinaisAltos[j].setVisible(false);
                arraySinaisBaixos[j].setVisible(true);
              } else if (!arraySinaisAltos[j - 1].isVisible() && !arraySinaisBaixos[j - 1].isVisible()) {
                arraySinaisAltos[j].setVisible(false);
                arraySinaisBaixos[j].setVisible(false);
              }
            }
            for (int k = 5; k > 0; k--) {
              if (((arraySinaisAltos[k - 1].isVisible() && !arraySinaisAltos[k].isVisible())
                  || (!arraySinaisAltos[k - 1].isVisible() && arraySinaisAltos[k].isVisible()))
                  && ((arraySinaisBaixos[k - 1].isVisible() && !arraySinaisBaixos[k].isVisible())
                      || (!arraySinaisBaixos[k - 1].isVisible() && arraySinaisBaixos[k].isVisible()))) {
                arrayTransicoes[k - 1].setVisible(true);
              } else {
                arrayTransicoes[k - 1].setVisible(false);
              }
            }
          });
          try {
            Thread.sleep(150); // Adiciona um pequeno intervalo entre as iteracoes
          } catch (InterruptedException e) {
            // e.printStackTrace();
          }
        }
        Principal.receptor.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB);

      } else if (sentido == 1) { // se o sentido e igual a 1, entao a informacao vai sair da direita para a
                                 // esquerda
        for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
          for (int j = 31; j >= 0; j--) {
            int bit = (fluxoBrutoDeBits[i] >> j) & 1;
            if (bit == 1) {
              Platform.runLater(() -> {
                arraySinaisAltos[5].setVisible(true);
                arraySinaisBaixos[5].setVisible(false);
                if (/* !arraySinaisAltos[1].isVisible()|| */arraySinaisBaixos[4].isVisible()) {
                  arrayTransicoes[4].setVisible(true);
                } else {
                  arrayTransicoes[4].setVisible(false);
                }
              });
            } else {
              Platform.runLater(() -> {
                arraySinaisAltos[5].setVisible(false);
                arraySinaisBaixos[5].setVisible(true);
                if (/* !arraySinaisBaixos[1].isVisible()|| */arraySinaisAltos[4].isVisible()) {
                  arrayTransicoes[4].setVisible(true);
                } else {
                  arrayTransicoes[4].setVisible(false);
                }
              });
            }
            try {
              Thread.sleep(50); // Adiciona um pequeno intervalo entre as iteracoes ALTERAR DEPOIS!!!
            } catch (InterruptedException e) {
              // e.printStackTrace();
            }

            // Atualiza a onda
            Platform.runLater(() -> {
              for (int k = 0; k < 5; k++) {
                if (arraySinaisAltos[k + 1].isVisible()) {
                  arraySinaisAltos[k].setVisible(true);
                  arraySinaisBaixos[k].setVisible(false);
                } else if (arraySinaisBaixos[k + 1].isVisible()) {
                  arraySinaisAltos[k].setVisible(false);
                  arraySinaisBaixos[k].setVisible(true);
                }
              }
              for (int k = 0; k < 5; k++) {
                if (((arraySinaisAltos[k + 1].isVisible() && !arraySinaisAltos[k].isVisible())
                    || (!arraySinaisAltos[k + 1].isVisible() && arraySinaisAltos[k].isVisible()))
                    && ((arraySinaisBaixos[k + 1].isVisible() && !arraySinaisBaixos[k].isVisible())
                        || (!arraySinaisBaixos[k + 1].isVisible() && arraySinaisBaixos[k].isVisible()))) {
                  arrayTransicoes[k].setVisible(true);
                } else {
                  arrayTransicoes[k].setVisible(false);
                }
              }
            });
            qtdBitsTotais--;
            if (qtdBitsTotais <= 0) {
              break;
            }
          }
          if (qtdBitsTotais <= 0) {
            break;
          }
        }
        Platform.runLater(() -> {
          arraySinaisAltos[5].setVisible(false);
          arraySinaisBaixos[5].setVisible(false);
        });
        try {
          Thread.sleep(150); // Adiciona um pequeno intervalo entre as iteracoes, ALTERAR DEPOIS!!!
        } catch (InterruptedException e) {
          // e.printStackTrace();
        }
        for (int i = 5; i > 0; i--) {
          Platform.runLater(() -> {
            for (int j = 0; j < 5; j++) {
              if (arraySinaisAltos[j + 1].isVisible()) {
                arraySinaisAltos[j].setVisible(true);
                arraySinaisBaixos[j].setVisible(false);
              } else if (arraySinaisBaixos[j + 1].isVisible()) {
                arraySinaisAltos[j].setVisible(false);
                arraySinaisBaixos[j].setVisible(true);
              } else if (!arraySinaisAltos[j + 1].isVisible() && !arraySinaisBaixos[j + 1].isVisible()) {
                arraySinaisAltos[j].setVisible(false);
                arraySinaisBaixos[j].setVisible(false);
              }
            }
            for (int k = 0; k < 5; k++) {
              if (((arraySinaisAltos[k + 1].isVisible() && !arraySinaisAltos[k].isVisible())
                  || (!arraySinaisAltos[k + 1].isVisible() && arraySinaisAltos[k].isVisible()))
                  && ((arraySinaisBaixos[k + 1].isVisible() && !arraySinaisBaixos[k].isVisible())
                      || (!arraySinaisBaixos[k + 1].isVisible() && arraySinaisBaixos[k].isVisible()))) {
                arrayTransicoes[k].setVisible(true);
              } else {
                arrayTransicoes[k].setVisible(false);
              }
            }
          });
          try {
            Thread.sleep(150); // Adiciona um pequeno intervalo entre as iteracoes
          } catch (InterruptedException e) {
            // e.printStackTrace();
          }
        }
      }

      // int [] fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
      // int [] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length];
      // for (int i = 0; i < fluxoBrutoDeBitsPontoA.length; i++){
      // fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i];
      // }
      Principal.semaforoMeioDeComunicacao.release();
    }).start();

  }// fim do metodo MeioDeTransmissao
}