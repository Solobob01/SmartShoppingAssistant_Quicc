#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int **callocMatrix(int n, int m){
    printf("%d %d\n", n, m);
    int** output = (int**)calloc(n, sizeof(int*));
    for(int i = 0; i < n;i++){
        output[i] = (int*)calloc(m, sizeof(int));
    }
    return output;
}

/*
n - numberOfItems
m - numberOfHistory
*/
int** readMatrix(int** matrix, int n, int m,FILE* pf){
    for(int i = 0;i  < n; i++){
        for(int j = 0; j < m;j++){
            fscanf(pf, "%d", &matrix[i][j]);
        }
    }
    return matrix;
}

void printMatrix(int** matrix, int n, int m){
    for(int i = 0; i < n;i++){
        for(int j = 0;j < m;j++){
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }
}

void freeMatrix(int** matrix, int n, int m){
    for(int i = 0; i < m;i++){
        free(matrix[i]);
    }
    free(matrix);
}

double findB(int** matrix, int n, int m, int item1, int item2){
    int rating1 = 0;
    int rating2 = 0;
    for(int i = 0;i < n;i++){
        rating1 += matrix[i][item1] * matrix[i][item1];
    }
    for(int i = 0;i < n;i++){
        rating2 += matrix[i][item2] * matrix[i][item2];
    }

    double sR1 = sqrt((double)rating1);
    double sR2 = sqrt((double)rating2);
    return sR1 * sR2;
}

double findA(int** matrix, int n, int m, int item1, int item2){
    double sum = 0;
    for(int i = 0;i < n;i++){
        sum += matrix[i][item1] * matrix[i][item2];
    }
    return sum;
}

double findSimilarity(int** matrix, int n, int m, int item1, int item2){
    double a = findA(matrix, n, m, item1, item2);
    double b = findB(matrix, n, m, item1, item2);
    if( b == 0){
        return -100;
    }
    printf("A:%f B:%f\n", a, b);
    return a/b;
}



int main(){
    FILE* inputFile = fopen("matrix.in", "r");
    int nrHistory, nrItems;
    fscanf(inputFile, "%d %d", &nrHistory, &nrItems);
    int** matrix = callocMatrix(nrHistory, nrItems);
    int** simMatrix = callocMatrix(nrHistory, nrItems);
    double sMatrix[3][3];
    matrix = readMatrix(matrix, nrHistory, nrItems, inputFile);
    for(int i = 0;i < nrItems;i++){
        for(int j = 0; j < nrItems;j++){
            if(i == j){
                sMatrix[i][j] = -1000;
            } else {
                sMatrix[i][j] = findSimilarity(matrix, nrHistory, nrItems, i, j);
            }
        }
    }
    for(int i = 0;i < 3;i++){
        for(int j = 0;j < 3;j++){
            printf("%f ", sMatrix[i][j]);
        }
        printf("\n");
    }
    fclose(inputFile);
    return 0;
}