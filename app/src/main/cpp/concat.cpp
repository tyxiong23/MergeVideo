#include <jni.h>

//
// Created by x-eri on 2022/1/23.
//

struct Node{
    float score; //累计总分数
    int num; //当前路径的视频数量
    int rank; //当前视频的编号
    Node* prev;
    Node(float _score = 0.0f, int _num = 0, int _rank = -1, Node* _prev = nullptr){
        score = _score; prev = _prev; num = _num; rank = _rank;
    }

};

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_myapplication_jni_ConcatUtils_dp(JNIEnv *env, jclass clazz,
                                                  jfloatArray video_scores,
                                                  jfloatArray concat_scores, jint len) {
    // TODO: implement dp()
    int length = len;
    jfloat* videoScores = env->GetFloatArrayElements(video_scores, JNI_FALSE);
    jfloat* concatScores = env->GetFloatArrayElements(concat_scores, JNI_FALSE);

    jint * result = new jint[length];
    for (int i = 0; i < length; ++i) {
        result[i] = 0;
    }

    Node ** rec = new Node*[length]; // rec[i][j] 当前已经经过了第i段视频，共选择了j+1条视频;
    for (int i = 0; i < length; ++i){
        rec[i] = new Node[length];
    }

    for (int i = 0; i < length; ++i){
        rec[i][0] = Node(videoScores[i], 1, 0);
    }
    for (int i = 1;i < length; ++i) {
        float score = rec[i-1][i-1].score+concatScores[(i-1)*length+i]+videoScores[i];
        rec[i][i] = Node(score, i + 1, i, &rec[i-1][i-1]);
    }
    for (int j = 1; j < length; ++j) {
        for (int i = j + 1; i < length; ++i) {
            Node *p = &rec[i-1][j];
            rec[i][j] = Node(p->score, j+1, i, p);
            for (int k = j - 1; k < i; k++) {
                Node* tmp = &rec[k][j-1];
                float tmp_score = tmp->score + concatScores[k*length+i] + videoScores[i];
                if (tmp_score > rec[i][j].score){
                    rec[i][j] = Node(tmp_score, j+1, i, tmp);
                }
            }
        }
    }
    float max_average = 0.0f; int max_j = -1;
    for (int j = 0; j < length; ++j) {
        Node* p = &rec[length-1][j];
        float tmp_average = p->score / p->num;
        if (tmp_average > max_average) {
            max_average = tmp_average;
            max_j = j;
        }
    }


    Node* p = &rec[length-1][max_j];
    while(p != nullptr){
        Node* p_prev = p->prev;
        if (p_prev == nullptr){
            result[p->rank] = 1;
            break;
        } else{
            if (p_prev->num < p->num){
                result[p->rank] = 1;
            }
            p = p_prev;
        }
    }


    jintArray resultArray = env->NewIntArray(length);
    env->SetIntArrayRegion(resultArray, 0, length, result);
    return resultArray;
}